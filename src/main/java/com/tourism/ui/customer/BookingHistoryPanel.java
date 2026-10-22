package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.UserDAO;
import com.tourism.model.Booking;
import com.tourism.model.User;
import com.tourism.service.BookingException;
import com.tourism.service.BookingService;
import com.tourism.service.PDFExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the logged-in customer's bookings in a table and lets them cancel one.
 * Use it with:  new BookingHistoryPanel(userId)
 * Concepts: JTable + table model, event handling, collections (List), exception handling.
 */
public class BookingHistoryPanel extends JPanel {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final int userId;
    private JLabel notificationLabel;
    private final BookingService bookingService = new BookingService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Booking> bookings = new ArrayList<>();   // same order as the table rows

    public BookingHistoryPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("My Bookings");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));

// Panel for heading and trip notification
        JPanel topPanel = new JPanel(new BorderLayout(5, 8));
        topPanel.add(heading, BorderLayout.NORTH);

// Reminder for upcoming trips
        notificationLabel = new JLabel();
        notificationLabel.setOpaque(true);
        notificationLabel.setBackground(new Color(255, 243, 205));
        notificationLabel.setForeground(new Color(120, 80, 0));
        notificationLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        topPanel.add(notificationLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Package", "Travel date", "Guests", "Total", "Status", "Refund"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);                   // cells are read-only
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);

        // Status column pill badge renderer
        table.getColumnModel().getColumn(5).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            String status = value != null ? value.toString() : "";
            JLabel pill = new JLabel(" " + status + " ", SwingConstants.CENTER);
            pill.setFont(pill.getFont().deriveFont(Font.BOLD, 11f));
            pill.setOpaque(true);

            if ("CONFIRMED".equalsIgnoreCase(status)) {
                pill.setBackground(new Color(232, 248, 245));
                pill.setForeground(new Color(39, 174, 96));
                pill.setBorder(BorderFactory.createLineBorder(new Color(39, 174, 96, 100), 1, true));
            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                pill.setBackground(new Color(253, 237, 236));
                pill.setForeground(new Color(231, 76, 60));
                pill.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60, 100), 1, true));
            } else {
                pill.setBackground(new Color(235, 245, 251));
                pill.setForeground(new Color(41, 128, 185));
                pill.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185, 100), 1, true));
            }
            return pill;
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton boardingPassBtn = new JButton("✈️ Boarding Pass");
        JButton downloadInvoiceBtn = new JButton("📄 Download PDF Invoice");
        JButton cancelButton = new JButton("Cancel selected booking");
        JButton reviewButton = new JButton("Write Review");

        boardingPassBtn.setBackground(new Color(232, 248, 245));
        boardingPassBtn.setForeground(new Color(22, 160, 133));
        boardingPassBtn.setFont(ThemeConstants.FONT_BOLD);
        boardingPassBtn.setFocusPainted(false);
        boardingPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        downloadInvoiceBtn.setBackground(new Color(235, 245, 251));
        downloadInvoiceBtn.setForeground(new Color(31, 97, 141));
        downloadInvoiceBtn.setFocusPainted(false);
        downloadInvoiceBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refreshButton.addActionListener(e -> loadBookings());
        boardingPassBtn.addActionListener(e -> onShowBoardingPass());
        downloadInvoiceBtn.addActionListener(e -> onDownloadInvoice());
        cancelButton.addActionListener(e -> onCancel());
        reviewButton.addActionListener(e -> onReview());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(refreshButton);
        buttons.add(boardingPassBtn);
        buttons.add(downloadInvoiceBtn);
        buttons.add(cancelButton);
        buttons.add(reviewButton);
        add(buttons, BorderLayout.SOUTH);

        loadBookings();
    }

    /** Reloads the table from the database. Call this after a new booking is made. */
    public void loadBookings() {
        try {
            bookings = bookingService.getUserBookings(userId);
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for trips starting within the next 7 days
        java.time.LocalDate today = java.time.LocalDate.now();
        int upcomingTrips = 0;

        for (Booking booking : bookings) {
            java.time.LocalDate travelDate = booking.getTravelDate();

            if (Booking.CONFIRMED.equals(booking.getStatus())
                    && travelDate != null
                    && !travelDate.isBefore(today)
                    && !travelDate.isAfter(today.plusDays(7))) {
                upcomingTrips++;
            }
        }

        if (upcomingTrips > 0) {
            notificationLabel.setText(
                    "Reminder: You have " + upcomingTrips
                            + " trip(s) departing within 7 days!"
            );
            notificationLabel.setVisible(true);
        } else {
            notificationLabel.setVisible(false);
        }
        com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();
        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            String refund = Booking.CANCELLED.equals(b.getStatus())
                    ? cm.format(b.getRefundAmount()) : "-";
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getPackageTitle(),
                    b.getTravelDate().format(DATE_FORMAT),
                    b.getNumberOfGuests(),
                    cm.format(b.getTotalCost()),
                    b.getStatus(),
                    refund
            });
        }
    }

    private void onCancel() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        Booking selected = bookings.get(row);
        if (Booking.CANCELLED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only confirmed bookings can be cancelled.");
            return;
        }

        double refund = bookingService.calculateRefund(selected);
        com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();
        String message = String.format(
                "Cancel booking #%d (%s)?%n%nYou will get a refund of %s.%n"
              + "Refund policy: 80%% if cancelled more than 48 hours before the trip, otherwise no refund.",
                selected.getBookingId(), selected.getPackageTitle(), cm.format(refund));

        int choice = JOptionPane.showConfirmDialog(this, message, "Confirm cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            double refunded = bookingService.cancelBooking(selected.getBookingId(), userId);
            JOptionPane.showMessageDialog(this, "Booking cancelled. Refund: " + cm.format(refunded));
            loadBookings();
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void onReview() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking first.");
            return;
        }

        Booking selected = bookings.get(row);

        if (!Booking.COMPLETED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "You can only review trips that have already started and are not cancelled.");
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        Frame owner = window instanceof Frame ? (Frame) window : null;

        ReviewDialog dialog = new ReviewDialog(
                owner,
                userId,
                selected.getPackageId(),
                selected.getPackageTitle()
        );

        dialog.setVisible(true);
    }

    private void onShowBoardingPass() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking from the table first.",
                    "No Booking Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Booking selected = bookings.get(row);
        if (Booking.CANCELLED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Boarding passes are not issued for cancelled bookings.",
                    "Booking Cancelled",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User customer = new UserDAO().findById(userId);
        if (customer == null) {
            customer = new User(userId, "Valued Traveler", "traveler@tourism.com", "", "CUSTOMER", "");
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        new BoardingPassDialog(owner, selected, customer).setVisible(true);
    }

    private void onDownloadInvoice() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a booking from the table first.",
                    "No Booking Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Booking selected = bookings.get(row);
        UserDAO userDAO = new UserDAO();
        User customer = userDAO.findById(userId);
        if (customer == null) {
            customer = new User(userId, "Valued Customer", "customer@tourism.com", "", "CUSTOMER", "");
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Booking Invoice PDF");
        fileChooser.setSelectedFile(new File("Invoice_BKG" + selected.getBookingId() + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            if (!targetFile.getName().toLowerCase().endsWith(".pdf")) {
                targetFile = new File(targetFile.getParentFile(), targetFile.getName() + ".pdf");
            }
            try {
                PDFExportService pdfService = new PDFExportService();
                pdfService.exportInvoicePDF(selected, customer, targetFile);
                JOptionPane.showMessageDialog(
                        this,
                        "Invoice PDF exported successfully!\nSaved to: " + targetFile.getAbsolutePath(),
                        "PDF Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to export invoice PDF:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
