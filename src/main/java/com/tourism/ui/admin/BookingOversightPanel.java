package com.tourism.ui.admin;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.BookingDAO;
import com.tourism.model.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Administrator view for reviewing bookings and filtering by status. */
public class BookingOversightPanel extends JPanel {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{"ALL", Booking.CONFIRMED, Booking.CANCELLED, Booking.COMPLETED});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Booking ID", "Customer ID", "Package", "Travel Date", "Guests", "Total Cost", "Status", "Refund", "Booked At"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JButton refreshButton = new JButton("Refresh");
    private final JLabel statusLabel = new JLabel(" ");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BookingOversightPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM,
                ThemeConstants.PADDING_MEDIUM, ThemeConstants.PADDING_MEDIUM));
        setBackground(ThemeConstants.BACKGROUND);

        JLabel title = new JLabel("Booking Oversight");
        title.setFont(ThemeConstants.FONT_HEADER_LARGE);
        title.setForeground(ThemeConstants.PRIMARY_DARK);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filters.add(new JLabel("Booking status:"));
        filters.add(statusFilter);
        filters.add(refreshButton);
        header.add(filters, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);
        statusLabel.setFont(ThemeConstants.FONT_SMALL);
        statusLabel.setForeground(ThemeConstants.TEXT_MUTED);
        add(statusLabel, BorderLayout.SOUTH);

        statusFilter.addActionListener(e -> loadBookings());
        refreshButton.addActionListener(e -> loadBookings());
        loadBookings();
    }

    private void loadBookings() {
        refreshButton.setEnabled(false);
        statusFilter.setEnabled(false);
        String selectedStatus = (String) statusFilter.getSelectedItem();
        statusLabel.setText("Loading bookings...");
        new SwingWorker<List<Booking>, Void>() {
            @Override protected List<Booking> doInBackground() throws SQLException {
                return bookingDAO.findAllForAdmin(selectedStatus);
            }
            @Override protected void done() {
                refreshButton.setEnabled(true);
                statusFilter.setEnabled(true);
                try {
                    List<Booking> bookings = get();
                    model.setRowCount(0);
                    com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();
                    for (Booking b : bookings) {
                        model.addRow(new Object[]{b.getBookingId(), b.getUserId(), b.getPackageTitle(),
                                b.getTravelDate(), b.getNumberOfGuests(), cm.format(b.getTotalCost()),
                                b.getBookingStatus(), b.getRefundAmount() > 0 ? cm.format(b.getRefundAmount()) : "-",
                                b.getBookingDate() == null ? "" : b.getBookingDate().format(DATE_TIME)});
                    }
                    statusLabel.setText(bookings.size() + " booking(s) shown.");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    statusLabel.setText("Loading was interrupted.");
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    statusLabel.setText("Could not load bookings.");
                    JOptionPane.showMessageDialog(BookingOversightPanel.this,
                            "Could not load bookings: " + (cause == null ? ex.getMessage() : cause.getMessage()),
                            "Booking Oversight", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
