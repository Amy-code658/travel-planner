package com.tourism.ui.customer;

import com.tourism.config.CurrencyManager;
import com.tourism.config.ThemeConstants;
import com.tourism.model.Booking;
import com.tourism.service.BookingService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modal dialog for booking cancellation with clear refund policy calculation
 * and high-visibility notification of the exact amount that will be credited back.
 */
public class CancelBookingDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private final Booking booking;
    private final double refundAmount;
    private final long hoursRemaining;
    private final boolean isEligibleForRefund;
    private boolean confirmed = false;

    public CancelBookingDialog(Window owner, Booking booking, BookingService bookingService) {
        super(owner, "Cancel Booking #BKG-" + booking.getBookingId(), ModalityType.APPLICATION_MODAL);
        this.booking = booking;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tripStart = booking.getTravelDate().atStartOfDay();
        this.hoursRemaining = Math.max(0, Duration.between(now, tripStart).toHours());
        this.refundAmount = bookingService.calculateRefund(booking);
        this.isEligibleForRefund = refundAmount > 0;

        initUI();
    }

    private void initUI() {
        setSize(560, 560);
        setMinimumSize(new Dimension(500, 520));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        CurrencyManager cm = CurrencyManager.getInstance();

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setBackground(new Color(254, 242, 242));
        headerPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(254, 202, 202)),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLabel = new JLabel("⚠️ Confirm Trip Cancellation");
        titleLabel.setFont(ThemeConstants.FONT_HEADER);
        titleLabel.setForeground(new Color(185, 28, 28));

        JLabel subLabel = new JLabel("Please review the cancellation policy and refund credit amount below.");
        subLabel.setFont(ThemeConstants.FONT_SMALL);
        subLabel.setForeground(new Color(127, 29, 29));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Cards
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Card A: Booking Details
        JPanel tripCard = createBaseCard("Trip Overview");
        JPanel tripGrid = new JPanel(new GridLayout(2, 2, 12, 8));
        tripGrid.setOpaque(false);
        tripGrid.add(createField("Booking Reference", "#BKG-" + booking.getBookingId()));
        tripGrid.add(createField("Package", booking.getPackageTitle()));
        tripGrid.add(createField("Travel Date", booking.getTravelDate().format(DATE_FORMAT)));
        tripGrid.add(createField("Guests", booking.getNumberOfGuests() + " Traveler" + (booking.getNumberOfGuests() > 1 ? "s" : "")));
        tripCard.add(tripGrid, BorderLayout.CENTER);
        contentPanel.add(tripCard);

        contentPanel.add(Box.createVerticalStrut(12));

        // Card B: Policy Calculation Details
        JPanel policyCard = createBaseCard("Cancellation Policy Breakdown");
        JPanel policyGrid = new JPanel(new GridLayout(3, 2, 12, 8));
        policyGrid.setOpaque(false);

        double originalTotal = booking.getTotalCost();
        double cancellationFee = Math.max(0.0, originalTotal - refundAmount);

        String noticeText = hoursRemaining + " hours before departure";
        String policyApplied = isEligibleForRefund
                ? "80% Refund (> 48 hrs notice)"
                : "0% Refund (<= 48 hrs cutoff)";

        policyGrid.add(createField("Departure Notice", noticeText));
        policyGrid.add(createField("Applied Rule", policyApplied));
        policyGrid.add(createField("Original Paid Amount", cm.format(originalTotal)));
        policyGrid.add(createField("Cancellation Fee", cm.format(cancellationFee)));
        policyGrid.add(createField("Refund Rate", isEligibleForRefund ? "80%" : "0% (Late Cancel)"));
        policyGrid.add(createField("Refund Status", isEligibleForRefund ? "Eligible" : "Non-refundable"));

        policyCard.add(policyGrid, BorderLayout.CENTER);
        contentPanel.add(policyCard);

        contentPanel.add(Box.createVerticalStrut(12));

        // Card C: High-Visibility Refund Notification Box
        JPanel refundBox = new JPanel(new BorderLayout(8, 8));
        refundBox.setBackground(isEligibleForRefund ? new Color(240, 253, 244) : new Color(254, 249, 231));
        refundBox.setBorder(new CompoundBorder(
                new LineBorder(isEligibleForRefund ? new Color(34, 197, 94) : new Color(245, 158, 11), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel refundHeader = new JLabel(isEligibleForRefund ? "💳 Amount to be Credited Back:" : "ℹ️ Non-Refundable Window");
        refundHeader.setFont(ThemeConstants.FONT_BOLD);
        refundHeader.setForeground(isEligibleForRefund ? new Color(21, 128, 61) : new Color(180, 83, 9));

        JLabel refundValue = new JLabel(cm.format(refundAmount));
        refundValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        refundValue.setForeground(isEligibleForRefund ? new Color(21, 128, 61) : new Color(180, 83, 9));

        String notifyMsg = isEligibleForRefund
                ? "<html><b>Refund Notification:</b> An amount of <b style='color:#15803d;'>" + cm.format(refundAmount)
                + "</b> will be credited back to your original payment method (Credit Card / UPI) within <b>3 to 5 business days</b>.</html>"
                : "<html><b>Notice:</b> Cancellations made within 48 hours of departure are non-refundable according to the standard policy. No amount will be credited back.</html>";

        JLabel notifyLabel = new JLabel(notifyMsg);
        notifyLabel.setFont(ThemeConstants.FONT_SMALL);

        JPanel refundCenter = new JPanel(new GridLayout(2, 1, 0, 4));
        refundCenter.setOpaque(false);
        refundCenter.add(refundValue);
        refundCenter.add(notifyLabel);

        refundBox.add(refundHeader, BorderLayout.NORTH);
        refundBox.add(refundCenter, BorderLayout.CENTER);

        contentPanel.add(refundBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Action Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton keepBtn = new JButton("Keep My Booking");
        keepBtn.setFont(ThemeConstants.FONT_BODY);
        keepBtn.setFocusPainted(false);
        keepBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        keepBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton cancelConfirmBtn = new JButton(isEligibleForRefund
                ? "Confirm Cancellation (" + cm.format(refundAmount) + " Refund)"
                : "Confirm Cancellation (No Refund)");
        cancelConfirmBtn.setFont(ThemeConstants.FONT_BOLD);
        cancelConfirmBtn.setBackground(new Color(220, 38, 38));
        cancelConfirmBtn.setForeground(Color.WHITE);
        cancelConfirmBtn.setFocusPainted(false);
        cancelConfirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelConfirmBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        footerPanel.add(keepBtn);
        footerPanel.add(cancelConfirmBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createBaseCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeConstants.FONT_BOLD);
        titleLbl.setForeground(ThemeConstants.PRIMARY_DARK);
        card.add(titleLbl, BorderLayout.NORTH);
        return card;
    }

    private JPanel createField(String label, String value) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeConstants.FONT_SMALL);
        lbl.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(ThemeConstants.FONT_BODY);
        val.setForeground(ThemeConstants.TEXT_PRIMARY);

        panel.add(lbl);
        panel.add(val);
        return panel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
}
