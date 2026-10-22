package com.tourism.ui.admin;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.AdminDAO;
import com.tourism.model.CustomerBookingDossier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Inspection drawer/modal for Administrators to inspect all customer and package details.
 * Financial amounts are deliberately masked into clear settlement badges (PAID, PENDING, REFUNDED).
 */
public class CustomerDossierDialog extends JDialog {

    private final CustomerBookingDossier dossier;
    private final AdminDAO adminDAO;
    private final Runnable onUpdatedCallback;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public CustomerDossierDialog(Window owner, CustomerBookingDossier dossier, Runnable onUpdatedCallback) {
        super(owner, "Customer & Booking Dossier — Ref #" + (dossier.hasBooking() ? dossier.getBookingId() : "N/A"), ModalityType.APPLICATION_MODAL);
        this.dossier = dossier;
        this.adminDAO = new AdminDAO();
        this.onUpdatedCallback = onUpdatedCallback;

        initComponents();
    }

    private void initComponents() {
        setSize(680, 640);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        // --- Header Banner ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeConstants.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titleLabel = new JLabel("Customer Travel Dossier");
        titleLabel.setFont(ThemeConstants.FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Comprehensive customer profile, selected package itinerary, and payment fulfillment status");
        subLabel.setFont(ThemeConstants.FONT_SMALL);
        subLabel.setForeground(new Color(200, 220, 240));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);
        titleBlock.add(titleLabel);
        titleBlock.add(subLabel);
        header.add(titleBlock, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // --- Content Body (Scrollable) ---
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(ThemeConstants.BACKGROUND);
        body.setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Customer Identity Card
        body.add(createCustomerCard());
        body.add(Box.createVerticalStrut(14));

        // 2. Chosen Package & Destination Card
        body.add(createPackageCard());
        body.add(Box.createVerticalStrut(14));

        // 3. Payment Fulfillment & Administrative Governance Card
        body.add(createPaymentFulfillmentCard());

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // --- Footer Toolbar ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 230, 235)));

        if (dossier.hasBooking()) {
            JButton updatePaymentBtn = new JButton("Update Settlement Status");
            updatePaymentBtn.setFont(ThemeConstants.FONT_BODY);
            updatePaymentBtn.setFocusPainted(false);
            updatePaymentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            updatePaymentBtn.addActionListener(e -> promptUpdatePayment());
            footer.add(updatePaymentBtn);
        }

        JButton closeBtn = new JButton("Close Dossier");
        closeBtn.setFont(ThemeConstants.FONT_BODY);
        closeBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCustomerCard() {
        JPanel card = createBaseCard("👤 Customer Profile (Sign-In Identity)");
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 10));
        grid.setOpaque(false);

        grid.add(createDetailItem("Full Name", dossier.getFullName()));
        grid.add(createDetailItem("Email Address", dossier.getEmail()));
        grid.add(createDetailItem("Phone Number", dossier.getPhone()));

        String registeredStr = (dossier.getUserCreatedAt() != null)
                ? dossier.getUserCreatedAt().format(TIME_FMT)
                : "Standard Registration";
        grid.add(createDetailItem("Account Registered Since", registeredStr));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPackageCard() {
        JPanel card = createBaseCard("🌴 Selected Travel Package & Destination");

        if (!dossier.hasBooking()) {
            JLabel emptyLabel = new JLabel("ℹ️ This customer signed in and registered, but has not booked a travel package yet.");
            emptyLabel.setFont(ThemeConstants.FONT_BODY);
            emptyLabel.setForeground(ThemeConstants.TEXT_MUTED);
            emptyLabel.setBorder(new EmptyBorder(8, 0, 8, 0));
            card.add(emptyLabel, BorderLayout.CENTER);
            return card;
        }

        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.setOpaque(false);

        // Destination thumbnail
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(110, 80));
        imgLabel.setBorder(new LineBorder(new Color(220, 225, 230), 1));
        try {
            java.net.URL imgUrl = getClass().getResource("/images/" + dossier.getDestinationImageUrl());
            if (imgUrl != null) {
                ImageIcon orig = new ImageIcon(imgUrl);
                Image scaled = orig.getImage().getScaledInstance(110, 80, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } else {
                imgLabel.setText("No Photo");
                imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception ignored) {
            imgLabel.setText("No Photo");
        }
        content.add(imgLabel, BorderLayout.WEST);

        JPanel details = new JPanel(new GridLayout(3, 2, 12, 6));
        details.setOpaque(false);

        details.add(createDetailItem("Package Title", dossier.getPackageTitle()));
        details.add(createDetailItem("Destination", dossier.getDestinationName() + ", " + dossier.getDestinationCountry()));
        details.add(createDetailItem("Category", dossier.getDestinationCategory()));
        details.add(createDetailItem("Duration", (dossier.getDurationDays() != null ? dossier.getDurationDays() + " Days" : "—")));

        String travelDateStr = (dossier.getTravelDate() != null) ? dossier.getTravelDate().format(DATE_FMT) : "Not Set";
        details.add(createDetailItem("Departure Date", travelDateStr));
        details.add(createDetailItem("Party Size", (dossier.getNumberOfGuests() != null ? dossier.getNumberOfGuests() + " Guests" : "1 Guest")));

        content.add(details, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPaymentFulfillmentCard() {
        JPanel card = createBaseCard("💳 Payment Fulfillment & Settlement Status");

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badgeRow.setOpaque(false);

        JLabel statusPill = createPaymentPill(dossier.getPaymentStatus());
        badgeRow.add(statusPill);

        JLabel tripPill = createTripPill(dossier.getBookingStatus());
        badgeRow.add(tripPill);

        panel.add(badgeRow, BorderLayout.NORTH);

        JPanel infoBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        infoBlock.setOpaque(false);
        infoBlock.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel notice = new JLabel("🔒 Sensitive transaction amounts are masked in compliance with admin privacy safeguards.");
        notice.setFont(ThemeConstants.FONT_SMALL);
        notice.setForeground(new Color(100, 115, 130));

        String bookingDateStr = (dossier.getBookingDate() != null)
                ? "Booking Logged: " + dossier.getBookingDate().format(TIME_FMT)
                : "Booking Status: Active Session";
        JLabel bookedOn = new JLabel("🕒 " + bookingDateStr + "  •  Fulfillment Channel: Instant Verification");
        bookedOn.setFont(ThemeConstants.FONT_SMALL);
        bookedOn.setForeground(ThemeConstants.TEXT_MUTED);

        infoBlock.add(notice);
        infoBlock.add(bookedOn);

        panel.add(infoBlock, BorderLayout.CENTER);
        card.add(panel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBaseCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 230, 235), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeConstants.FONT_BOLD);
        titleLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JPanel createDetailItem(String label, String value) {
        JPanel item = new JPanel(new GridLayout(2, 1, 0, 2));
        item.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConstants.FONT_SMALL);
        lbl.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel val = new JLabel(value != null && !value.isBlank() ? value : "—");
        val.setFont(ThemeConstants.FONT_BODY);
        val.setForeground(ThemeConstants.TEXT_PRIMARY);

        item.add(lbl);
        item.add(val);
        return item;
    }

    private JLabel createPaymentPill(String status) {
        JLabel pill = new JLabel("  " + status.toUpperCase() + "  ");
        pill.setFont(ThemeConstants.FONT_BOLD);
        pill.setOpaque(true);
        pill.setBorder(new EmptyBorder(4, 8, 4, 8));

        switch (status.toUpperCase()) {
            case "PAID":
                pill.setText("  ● PAID IN FULL  ");
                pill.setBackground(new Color(212, 239, 223)); // Mint green
                pill.setForeground(new Color(20, 90, 50));
                break;
            case "PENDING":
                pill.setText("  ● PAYMENT PENDING  ");
                pill.setBackground(new Color(254, 249, 231)); // Soft Amber
                pill.setForeground(new Color(125, 102, 8));
                break;
            case "PARTIALLY PAID":
            case "PARTIAL":
                pill.setText("  ● PARTIALLY PAID (DEPOSIT)  ");
                pill.setBackground(new Color(235, 245, 251)); // Soft Blue
                pill.setForeground(new Color(27, 79, 114));
                break;
            case "REFUNDED":
                pill.setText("  ● REFUNDED / CANCELLED  ");
                pill.setBackground(new Color(253, 237, 236)); // Soft Rose
                pill.setForeground(new Color(120, 40, 31));
                break;
            default:
                pill.setText("  ○ NOT PAID  ");
                pill.setBackground(new Color(234, 237, 237));
                pill.setForeground(new Color(93, 109, 126));
                break;
        }
        return pill;
    }

    private JLabel createTripPill(String status) {
        JLabel pill = new JLabel("  Trip: " + status.toUpperCase() + "  ");
        pill.setFont(ThemeConstants.FONT_SMALL);
        pill.setOpaque(true);
        pill.setBorder(new EmptyBorder(4, 8, 4, 8));

        switch (status.toUpperCase()) {
            case "CONFIRMED":
                pill.setBackground(new Color(232, 248, 245));
                pill.setForeground(new Color(17, 122, 101));
                break;
            case "COMPLETED":
                pill.setBackground(new Color(234, 250, 234));
                pill.setForeground(new Color(30, 132, 73));
                break;
            case "CANCELLED":
                pill.setBackground(new Color(250, 219, 216));
                pill.setForeground(new Color(148, 49, 38));
                break;
            default:
                pill.setBackground(new Color(242, 244, 244));
                pill.setForeground(new Color(86, 101, 115));
                break;
        }
        return pill;
    }

    private void promptUpdatePayment() {
        String[] options = {"PAID", "PENDING", "PARTIALLY PAID", "REFUNDED"};
        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Select updated payment status for Booking #" + dossier.getBookingId() + ":",
                "Update Settlement Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                dossier.getPaymentStatus()
        );

        if (chosen != null && !chosen.equalsIgnoreCase(dossier.getPaymentStatus())) {
            boolean success = adminDAO.updatePaymentStatus(dossier.getBookingId(), chosen);
            if (success) {
                dossier.setPaymentStatus(chosen);
                JOptionPane.showMessageDialog(this,
                        "Settlement status successfully updated to: " + chosen,
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                if (onUpdatedCallback != null) {
                    onUpdatedCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update status in database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
