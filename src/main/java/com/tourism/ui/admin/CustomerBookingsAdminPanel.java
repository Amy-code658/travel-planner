package com.tourism.ui.admin;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.AdminDAO;
import com.tourism.model.CustomerBookingDossier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise Admin Dashboard Panel: Customer Roster, Package Selections & Payment Settlements.
 * Designed to resemble modern SaaS admin dashboards (Stripe / Retool / Shopify Orders).
 * Financial dollar amounts are withheld in favor of clear fulfillment states (PAID, PENDING, REFUNDED).
 */
public class CustomerBookingsAdminPanel extends JPanel {

    private final AdminDAO adminDAO;
    private List<CustomerBookingDossier> dossiers;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField searchField;
    private JComboBox<String> paymentFilter;
    private JComboBox<String> tripFilter;

    // KPI Card Labels
    private JLabel totalCustomersVal;
    private JLabel totalBookingsVal;
    private JLabel paidCountVal;
    private JLabel pendingCountVal;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public CustomerBookingsAdminPanel() {
        this.adminDAO = new AdminDAO();
        this.dossiers = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND);

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        // 1. Top KPI Summary Metrics Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(14, 18, 10, 18));

        totalCustomersVal = new JLabel("0");
        totalBookingsVal = new JLabel("0");
        paidCountVal = new JLabel("0");
        pendingCountVal = new JLabel("0");

        kpiGrid.add(createKPICard("👥", "Total Customers", totalCustomersVal, "Registered in system", ThemeConstants.PRIMARY_COLOR));
        kpiGrid.add(createKPICard("✈️", "Trips Selected", totalBookingsVal, "Packages with bookings", new Color(39, 174, 96)));
        kpiGrid.add(createKPICard("✅", "Settled / Paid", paidCountVal, "Payment confirmed", new Color(22, 160, 133)));
        kpiGrid.add(createKPICard("⏳", "Pending / Action", pendingCountVal, "Awaiting fulfillment", new Color(230, 126, 34)));

        topContainer.add(kpiGrid, BorderLayout.NORTH);

        // 2. Search & Filter Bar
        JPanel controlBar = new JPanel(new BorderLayout(12, 0));
        controlBar.setBackground(Color.WHITE);
        controlBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(225, 230, 235)),
                new EmptyBorder(10, 18, 10, 18)
        ));

        // Left Controls: Search Bar
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftControls.setOpaque(false);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        leftControls.add(searchIcon);

        searchField = new JTextField(20);
        searchField.setFont(ThemeConstants.FONT_BODY);
        searchField.putClientProperty("JTextField.placeholderText", "Search by customer, email, package or destination...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        leftControls.add(searchField);

        // Filters
        paymentFilter = new JComboBox<>(new String[]{"All Settlements", "PAID", "PENDING", "PARTIALLY PAID", "REFUNDED", "NOT PAID"});
        paymentFilter.setFont(ThemeConstants.FONT_SMALL);
        paymentFilter.addActionListener(e -> applyFilters());
        leftControls.add(new JLabel("Settlement:"));
        leftControls.add(paymentFilter);

        tripFilter = new JComboBox<>(new String[]{"All Trip States", "CONFIRMED", "COMPLETED", "CANCELLED", "BROWSING"});
        tripFilter.setFont(ThemeConstants.FONT_SMALL);
        tripFilter.addActionListener(e -> applyFilters());
        leftControls.add(new JLabel("Trip State:"));
        leftControls.add(tripFilter);

        controlBar.add(leftControls, BorderLayout.WEST);

        // Right Controls: Action Buttons
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(ThemeConstants.FONT_SMALL);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());
        rightControls.add(refreshBtn);

        JButton exportBtn = new JButton("📄 Export Roster (CSV)");
        exportBtn.setFont(ThemeConstants.FONT_SMALL);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> exportToCSV());
        rightControls.add(exportBtn);

        JButton inspectBtn = new JButton("🔍 Inspect Dossier");
        inspectBtn.setFont(ThemeConstants.FONT_SMALL);
        inspectBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        inspectBtn.setForeground(Color.WHITE);
        inspectBtn.setFocusPainted(false);
        inspectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inspectBtn.addActionListener(e -> openSelectedDossier());
        rightControls.add(inspectBtn);

        controlBar.add(rightControls, BorderLayout.EAST);
        topContainer.add(controlBar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // 3. Central Modern Data Table
        String[] columnNames = {
                "Ref #", "Customer Profile", "Phone", "Selected Travel Package",
                "Destination", "Departure Date", "Party", "Payment Settlement", "Trip Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only enterprise grid
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setFont(ThemeConstants.FONT_BODY);
        table.getTableHeader().setFont(ThemeConstants.FONT_BOLD);
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.getTableHeader().setForeground(ThemeConstants.TEXT_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(235, 240, 245));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(55);  // Ref #
        table.getColumnModel().getColumn(1).setPreferredWidth(170); // Customer
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Phone
        table.getColumnModel().getColumn(3).setPreferredWidth(210); // Package
        table.getColumnModel().getColumn(4).setPreferredWidth(140); // Destination
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(6).setPreferredWidth(70);  // Party
        table.getColumnModel().getColumn(7).setPreferredWidth(130); // Payment Status
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Trip Status

        // Custom Cell Renderers
        table.getColumnModel().getColumn(1).setCellRenderer(new CustomerProfileRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new PaymentPillRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new TripStatusPillRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedDossier();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(12, 18, 12, 18));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        dossiers = adminDAO.getAllCustomerBookings();
        tableModel.setRowCount(0);

        int totalCustomers = 0;
        int totalBookings = 0;
        int paidCount = 0;
        int pendingCount = 0;

        java.util.Set<Integer> uniqueCustomerIds = new java.util.HashSet<>();

        for (CustomerBookingDossier d : dossiers) {
            uniqueCustomerIds.add(d.getUserId());

            String ref = d.hasBooking() ? "BKG-" + d.getBookingId() : "USR-" + d.getUserId();
            String profile = d.getFullName() + "|" + d.getEmail();
            String phone = d.getPhone();
            String pkg = d.getPackageTitle();
            String dest = d.getDestinationName() + (!d.getDestinationCountry().isBlank() ? ", " + d.getDestinationCountry() : "");
            String date = (d.getTravelDate() != null) ? d.getTravelDate().format(DATE_FMT) : "—";
            String party = (d.getNumberOfGuests() != null) ? "👥 " + d.getNumberOfGuests() : "—";
            String payment = d.getPaymentStatus();
            String trip = d.getBookingStatus();

            tableModel.addRow(new Object[]{ref, profile, phone, pkg, dest, date, party, payment, trip});

            if (d.hasBooking()) {
                totalBookings++;
                if ("PAID".equalsIgnoreCase(payment)) {
                    paidCount++;
                } else if ("PENDING".equalsIgnoreCase(payment) || "NOT PAID".equalsIgnoreCase(payment) || "PARTIALLY PAID".equalsIgnoreCase(payment)) {
                    pendingCount++;
                }
            }
        }

        totalCustomers = uniqueCustomerIds.size();

        totalCustomersVal.setText(totalCustomers + " Customers");
        totalBookingsVal.setText(totalBookings + " Booked Trips");
        paidCountVal.setText(paidCount + " Settled");
        pendingCountVal.setText(pendingCount + " Pending");
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String payment = (String) paymentFilter.getSelectedItem();
        String trip = (String) tripFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Search filter (across customer profile, package, destination, phone)
                if (!searchText.isEmpty()) {
                    boolean matches = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        String val = entry.getStringValue(i).toLowerCase();
                        if (val.contains(searchText)) {
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) return false;
                }

                // Payment Status Filter
                if (payment != null && !payment.equals("All Settlements")) {
                    String rowPayment = entry.getStringValue(7);
                    if (!rowPayment.equalsIgnoreCase(payment)) {
                        return false;
                    }
                }

                // Trip Status Filter
                if (trip != null && !trip.equals("All Trip States")) {
                    String rowTrip = entry.getStringValue(8);
                    if (!rowTrip.equalsIgnoreCase(trip)) {
                        return false;
                    }
                }

                return true;
            }
        };
        rowSorter.setRowFilter(rf);
    }

    private void openSelectedDossier() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer record from the table to view their dossier.",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        if (modelRow >= 0 && modelRow < dossiers.size()) {
            CustomerBookingDossier dossier = dossiers.get(modelRow);
            Window window = SwingUtilities.getWindowAncestor(this);
            CustomerDossierDialog dialog = new CustomerDossierDialog(window, dossier, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void exportToCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("tourism_customer_roster.csv"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("Reference,Customer Name,Email,Phone,Package Title,Destination,Departure Date,Party Size,Payment Status,Trip Status");
                for (CustomerBookingDossier d : dossiers) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            d.hasBooking() ? "BKG-" + d.getBookingId() : "USR-" + d.getUserId(),
                            d.getFullName().replace("\"", "\"\""),
                            d.getEmail(),
                            d.getPhone(),
                            d.getPackageTitle().replace("\"", "\"\""),
                            (d.getDestinationName() + ", " + d.getDestinationCountry()).replace("\"", "\"\""),
                            d.getTravelDate() != null ? d.getTravelDate().toString() : "N/A",
                            d.getNumberOfGuests() != null ? d.getNumberOfGuests() : "0",
                            d.getPaymentStatus(),
                            d.getBookingStatus()
                    );
                }
                JOptionPane.showMessageDialog(this,
                        "Successfully exported " + dossiers.size() + " records to:\n" + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createKPICard(String icon, String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(225, 230, 235), 1),
                        new EmptyBorder(10, 14, 10, 14)
                )
        ));

        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(ThemeConstants.FONT_SMALL);
        titleLabel.setForeground(ThemeConstants.TEXT_MUTED);
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(ThemeConstants.FONT_HEADER);
        valueLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(ThemeConstants.FONT_SMALL);
        subLabel.setForeground(accentColor);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    // --- Custom Table Cell Renderers ---

    /**
     * Renders customer name in bold with secondary email underneath (like Stripe/Retool).
     */
    private static class CustomerProfileRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
            panel.setOpaque(true);
            panel.setBorder(new EmptyBorder(4, 8, 4, 8));

            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
            }

            String str = (value != null) ? value.toString() : "";
            String[] parts = str.split("\\|", 2);
            String name = parts.length > 0 ? parts[0] : "";
            String email = parts.length > 1 ? parts[1] : "";

            JLabel nameLbl = new JLabel(name);
            nameLbl.setFont(ThemeConstants.FONT_BOLD);
            nameLbl.setForeground(isSelected ? table.getSelectionForeground() : ThemeConstants.TEXT_PRIMARY);

            JLabel emailLbl = new JLabel(email);
            emailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            emailLbl.setForeground(isSelected ? table.getSelectionForeground() : ThemeConstants.TEXT_MUTED);

            panel.add(nameLbl);
            panel.add(emailLbl);
            return panel;
        }
    }

    /**
     * Renders payment settlement as an enterprise pill badge (PAID, PENDING, REFUNDED).
     */
    private static class PaymentPillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            wrapper.setOpaque(true);
            wrapper.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252)));

            String status = (value != null) ? value.toString().toUpperCase() : "NOT PAID";
            JLabel pill = new JLabel();
            pill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pill.setOpaque(true);
            pill.setBorder(new EmptyBorder(3, 8, 3, 8));

            switch (status) {
                case "PAID":
                    pill.setText("● PAID");
                    pill.setBackground(new Color(212, 239, 223)); // Mint green
                    pill.setForeground(new Color(20, 90, 50));
                    break;
                case "PENDING":
                    pill.setText("● PENDING");
                    pill.setBackground(new Color(254, 249, 231)); // Soft amber
                    pill.setForeground(new Color(125, 102, 8));
                    break;
                case "PARTIALLY PAID":
                case "PARTIAL":
                    pill.setText("● PARTIAL");
                    pill.setBackground(new Color(243, 232, 255)); // Soft lavender
                    pill.setForeground(new Color(107, 33, 168));
                    break;
                case "REFUNDED":
                    pill.setText("● REFUNDED");
                    pill.setBackground(new Color(253, 237, 236)); // Soft rose
                    pill.setForeground(new Color(120, 40, 31));
                    break;
                default:
                    pill.setText("○ NOT PAID");
                    pill.setBackground(new Color(234, 237, 237)); // Slate
                    pill.setForeground(new Color(93, 109, 126));
                    break;
            }

            wrapper.add(pill);
            return wrapper;
        }
    }

    /**
     * Renders travel status as a subtle status tag (CONFIRMED, COMPLETED, CANCELLED, BROWSING).
     */
    private static class TripStatusPillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            wrapper.setOpaque(true);
            wrapper.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252)));

            String status = (value != null) ? value.toString().toUpperCase() : "BROWSING";
            JLabel pill = new JLabel();
            pill.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            pill.setOpaque(true);
            pill.setBorder(new EmptyBorder(3, 8, 3, 8));

            switch (status) {
                case "CONFIRMED":
                    pill.setText("Confirmed");
                    pill.setBackground(new Color(232, 248, 245));
                    pill.setForeground(new Color(17, 122, 101));
                    break;
                case "COMPLETED":
                    pill.setText("Completed");
                    pill.setBackground(new Color(234, 250, 234));
                    pill.setForeground(new Color(30, 132, 73));
                    break;
                case "CANCELLED":
                    pill.setText("Cancelled");
                    pill.setBackground(new Color(250, 219, 216));
                    pill.setForeground(new Color(148, 49, 38));
                    break;
                default:
                    pill.setText("Browsing");
                    pill.setBackground(new Color(242, 244, 244));
                    pill.setForeground(new Color(86, 101, 115));
                    break;
            }

            wrapper.add(pill);
            return wrapper;
        }
    }
}
