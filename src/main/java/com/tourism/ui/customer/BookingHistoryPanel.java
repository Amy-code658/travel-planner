package com.tourism.ui.customer;

import com.tourism.model.Booking;
import com.tourism.service.BookingException;
import com.tourism.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        add(heading, BorderLayout.NORTH);

        String[] columns = {"ID", "Package", "Travel date", "Guests", "Total", "Status", "Refund"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);                   // cells are read-only
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton cancelButton = new JButton("Cancel selected booking");
        refreshButton.addActionListener(e -> loadBookings());
        cancelButton.addActionListener(e -> onCancel());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(refreshButton);
        buttons.add(cancelButton);
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

        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            String refund = Booking.CANCELLED.equals(b.getStatus())
                    ? String.format("Rs. %.2f", b.getRefundAmount()) : "-";
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getPackageTitle(),
                    b.getTravelDate().format(DATE_FORMAT),
                    b.getNumberOfGuests(),
                    String.format("Rs. %.2f", b.getTotalCost()),
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
        if (!Booking.CONFIRMED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only confirmed bookings can be cancelled.");
            return;
        }

        double refund = bookingService.calculateRefund(selected);
        String message = String.format(
                "Cancel booking #%d (%s)?%n%nYou will get a refund of Rs. %.2f.%n"
              + "Refund policy: 80%% if cancelled more than 48 hours before the trip, otherwise no refund.",
                selected.getBookingId(), selected.getPackageTitle(), refund);

        int choice = JOptionPane.showConfirmDialog(this, message, "Confirm cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            double refunded = bookingService.cancelBooking(selected.getBookingId(), userId);
            JOptionPane.showMessageDialog(this, String.format("Booking cancelled. Refund: Rs. %.2f", refunded));
            loadBookings();
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
