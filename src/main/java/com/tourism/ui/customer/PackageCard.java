package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Reusable card component for displaying travel packages in a responsive layout.
 * Conforms to Pod 2 contract between Dev 3 (Catalog) and Dev 4 (Recommendations).
 */
public class PackageCard extends JPanel {

    private final Package pkg;
    private Consumer<Package> onViewDetailsListener;
    private Consumer<Package> onBookNowListener;

    private JLabel matchScoreLabel;
    private JLabel matchReasonLabel;

    public PackageCard(Package pkg) {
        this.pkg = pkg;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 10));
        setBackground(ThemeConstants.CARD_BG);
        setBorder(new CompoundBorder(
            new LineBorder(new Color(225, 230, 235), 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));

        // Header Panel (Category Pill + Title + Optional Match Score)
        JPanel topPanel = new JPanel(new BorderLayout(8, 4));
        topPanel.setOpaque(false);

        JPanel titleAndPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleAndPill.setOpaque(false);

        Destination dest = (pkg != null) ? pkg.getDestination() : null;
        String category = (dest != null && dest.getCategory() != null) ? dest.getCategory() : "General";

        // Category Pill
        JLabel categoryPill = new JLabel(category.toUpperCase());
        categoryPill.setOpaque(true);
        categoryPill.setBackground(new Color(235, 243, 250));
        categoryPill.setForeground(ThemeConstants.PRIMARY_COLOR);
        categoryPill.setFont(ThemeConstants.FONT_SMALL);
        categoryPill.setBorder(new EmptyBorder(3, 8, 3, 8));
        titleAndPill.add(categoryPill);

        JLabel titleLabel = new JLabel((pkg != null) ? pkg.getTitle() : "Travel Package");
        titleLabel.setFont(ThemeConstants.FONT_HEADER);
        titleLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        titleAndPill.add(titleLabel);

        topPanel.add(titleAndPill, BorderLayout.WEST);

        // Match Score Badge placeholder (for Dev 4 recommendations)
        matchScoreLabel = new JLabel();
        matchScoreLabel.setOpaque(true);
        matchScoreLabel.setFont(ThemeConstants.FONT_BOLD);
        matchScoreLabel.setForeground(Color.WHITE);
        matchScoreLabel.setVisible(false);
        topPanel.add(matchScoreLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Content (Description, Destination info, and Recommendation reason)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        String destName = (dest != null) ? dest.getName() + ", " + dest.getCountry() : "Worldwide";
        int days = (pkg != null) ? pkg.getDurationDays() : 0;
        String season = (pkg != null && pkg.getSeason() != null) ? pkg.getSeason() : "All Season";

        JLabel metaLabel = new JLabel("📍 " + destName + "   •   ⏱️ " + days + " Days   •   🌤️ " + season);
        metaLabel.setFont(ThemeConstants.FONT_BODY);
        metaLabel.setForeground(ThemeConstants.TEXT_MUTED);
        centerPanel.add(metaLabel);

        if (pkg != null && pkg.getDescription() != null && !pkg.getDescription().isBlank()) {
            centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            JLabel descLabel = new JLabel(pkg.getDescription());
            descLabel.setFont(ThemeConstants.FONT_BODY);
            descLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
            centerPanel.add(descLabel);
        }

        matchReasonLabel = new JLabel();
        matchReasonLabel.setFont(ThemeConstants.FONT_BOLD);
        matchReasonLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        matchReasonLabel.setVisible(false);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(matchReasonLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Action & Pricing Row
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        // Pricing Panel
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pricePanel.setOpaque(false);

        if (pkg != null) {
            double finalPrice = pkg.getFinalPrice();
            double basePrice = pkg.getBasePrice();
            double discount = pkg.getDiscountPercentage();

            if (discount > 0) {
                JLabel originalPriceLbl = new JLabel("$" + String.format("%.2f", basePrice));
                originalPriceLbl.setFont(ThemeConstants.FONT_SMALL);
                originalPriceLbl.setForeground(ThemeConstants.TEXT_MUTED);
                pricePanel.add(originalPriceLbl);

                JLabel discountBadge = new JLabel("-" + (int) discount + "%");
                discountBadge.setOpaque(true);
                discountBadge.setBackground(new Color(254, 237, 236));
                discountBadge.setForeground(ThemeConstants.DANGER_COLOR);
                discountBadge.setFont(ThemeConstants.FONT_SMALL);
                discountBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
                pricePanel.add(discountBadge);
            }

            JLabel finalPriceLbl = new JLabel("$" + String.format("%.2f", finalPrice) + " / person");
            finalPriceLbl.setFont(ThemeConstants.FONT_HEADER);
            finalPriceLbl.setForeground(ThemeConstants.ACCENT_COLOR);
            pricePanel.add(finalPriceLbl);
        }

        bottomRow.add(pricePanel, BorderLayout.WEST);

        // Action Buttons (Details & Book)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton detailsBtn = new JButton("View Itinerary");
        detailsBtn.setFont(ThemeConstants.FONT_BODY);
        detailsBtn.setFocusPainted(false);
        detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsBtn.addActionListener(e -> {
            if (onViewDetailsListener != null) {
                onViewDetailsListener.accept(pkg);
            }
        });

        JButton bookBtn = new JButton("Book Now");
        bookBtn.setFont(ThemeConstants.FONT_BOLD);
        bookBtn.setBackground(ThemeConstants.ACCENT_COLOR);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> {
            if (onBookNowListener != null) {
                onBookNowListener.accept(pkg);
            }
        });

        btnPanel.add(detailsBtn);
        btnPanel.add(bookBtn);

        bottomRow.add(btnPanel, BorderLayout.EAST);
        add(bottomRow, BorderLayout.SOUTH);
    }

    /**
     * Attaches recommendation match score details to the card.
     */
    public void setRecommendationScore(int scorePercentage, String matchSummary) {
        matchScoreLabel.setText(" " + scorePercentage + "% Match ");
        matchScoreLabel.setBackground(scorePercentage >= 80 ? ThemeConstants.ACCENT_COLOR : ThemeConstants.WARNING_COLOR);
        matchScoreLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        matchScoreLabel.setVisible(true);

        if (matchSummary != null && !matchSummary.isBlank()) {
            matchReasonLabel.setText("💡 " + matchSummary);
            matchReasonLabel.setVisible(true);
        }
    }

    public void setOnViewDetailsListener(Consumer<Package> listener) {
        this.onViewDetailsListener = listener;
    }

    public void setOnBookNowListener(Consumer<Package> listener) {
        this.onBookNowListener = listener;
    }

    public Package getPackage() {
        return pkg;
    }
}
