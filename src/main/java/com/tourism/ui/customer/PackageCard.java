package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Visual card component for displaying an individual travel package.
 * Styled using ThemeConstants and FlatLaf design standards.
 * Supports both Catalog grid browsing (Dev 3) and Recommendation score matching (Dev 4).
 */
public class PackageCard extends JPanel {

    private final Package travelPackage;
    private Consumer<Package> onViewDetailsListener;
    private Consumer<Package> onBookNowListener;

    private JLabel matchScoreLabel;
    private JLabel matchReasonLabel;

    public PackageCard(Package travelPackage) {
        this.travelPackage = travelPackage;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.CARD_BG);
        setPreferredSize(new Dimension(300, 400));
        setMinimumSize(new Dimension(280, 380));

        // Modern card border with subtle outline and padding
        setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 230, 238), 1, true),
                new EmptyBorder(0, 0, ThemeConstants.PADDING_MEDIUM, 0)
        ));

        // 1. Header Banner / Image section (NORTH)
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // 2. Body Details (CENTER)
        JPanel bodyPanel = createBodyPanel();
        add(bodyPanel, BorderLayout.CENTER);

        // 3. Price & Actions Footer (SOUTH)
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the top card header. Attempts to load image from classpath;
     * otherwise falls back to a category-styled gradient header.
     */
    private JPanel createHeaderBanner() {
        Destination dest = (travelPackage != null) ? travelPackage.getDestination() : null;
        String imageUrl = (dest != null) ? dest.getImageUrl() : null;
        String category = (dest != null && dest.getCategory() != null) ? dest.getCategory() : "Tour";

        Image loadedImage = null;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            URL resource = getClass().getResource("/images/" + imageUrl.trim());
            if (resource == null) {
                resource = getClass().getResource("/" + imageUrl.trim());
            }
            if (resource != null) {
                loadedImage = new ImageIcon(resource).getImage();
            }
        }

        final Image bannerImage = loadedImage;

        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                if (bannerImage != null) {
                    g2.drawImage(bannerImage, 0, 0, width, height, this);
                    GradientPaint overlay = new GradientPaint(
                            0, 0, new Color(0, 0, 0, 30),
                            0, height, new Color(0, 0, 0, 110)
                    );
                    g2.setPaint(overlay);
                    g2.fillRect(0, 0, width, height);
                } else {
                    // Category-based smooth gradient fallback
                    Color topColor = ThemeConstants.PRIMARY_DARK;
                    Color bottomColor = ThemeConstants.PRIMARY_COLOR;

                    if ("Beach".equalsIgnoreCase(category)) {
                        topColor = new Color(22, 160, 133);
                        bottomColor = new Color(41, 128, 185);
                    } else if ("Adventure".equalsIgnoreCase(category)) {
                        topColor = new Color(211, 84, 0);
                        bottomColor = new Color(230, 126, 34);
                    } else if ("Culture".equalsIgnoreCase(category)) {
                        topColor = new Color(142, 68, 173);
                        bottomColor = new Color(155, 89, 182);
                    } else if ("Nature".equalsIgnoreCase(category)) {
                        topColor = new Color(39, 174, 96);
                        bottomColor = new Color(46, 204, 113);
                    } else if ("Luxury".equalsIgnoreCase(category)) {
                        topColor = new Color(44, 62, 80);
                        bottomColor = new Color(52, 73, 94);
                    }

                    GradientPaint gradient = new GradientPaint(0, 0, topColor, width, height, bottomColor);
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, width, height);
                }
                g2.dispose();
            }
        };

        banner.setPreferredSize(new Dimension(300, 130));
        banner.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Top Row: Category pill (WEST) + Match Score Badge (EAST)
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel categoryBadge = createCategoryPill(category);
        topRow.add(categoryBadge, BorderLayout.WEST);

        // Match Score Badge placeholder (for Dev 4 recommendations)
        matchScoreLabel = new JLabel();
        matchScoreLabel.setOpaque(true);
        matchScoreLabel.setFont(ThemeConstants.FONT_BOLD);
        matchScoreLabel.setForeground(Color.WHITE);
        matchScoreLabel.setVisible(false);
        topRow.add(matchScoreLabel, BorderLayout.EAST);

        // Destination title on bottom-left of banner
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        String destLocation = (dest != null) ? dest.getName() + ", " + dest.getCountry() : "Worldwide";
        JLabel locationLabel = new JLabel("📍 " + destLocation);
        locationLabel.setFont(ThemeConstants.FONT_BOLD);
        locationLabel.setForeground(Color.WHITE);
        bottomRow.add(locationLabel, BorderLayout.SOUTH);

        banner.add(topRow, BorderLayout.NORTH);
        banner.add(bottomRow, BorderLayout.SOUTH);

        return banner;
    }

    /**
     * Creates the card body containing title, tags (duration, season), recommendation reason, and description.
     */
    private JPanel createBodyPanel() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(10, 14, 6, 14));

        String title = (travelPackage != null && travelPackage.getTitle() != null) ? travelPackage.getTitle() : "Travel Package";
        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(titleLabel);

        body.add(Box.createRigidArea(new Dimension(0, 6)));

        // Tags Row: Duration & Season
        JPanel tagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tagsRow.setOpaque(false);
        tagsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        int duration = (travelPackage != null) ? travelPackage.getDurationDays() : 0;
        JLabel durationTag = createTag("⏱ " + duration + " Days", new Color(240, 243, 246), ThemeConstants.TEXT_PRIMARY);
        tagsRow.add(durationTag);

        if (travelPackage != null && travelPackage.getSeason() != null && !travelPackage.getSeason().trim().isEmpty()) {
            JLabel seasonTag = createTag("☀️ " + travelPackage.getSeason(), new Color(254, 249, 231), new Color(183, 119, 0));
            tagsRow.add(seasonTag);
        }

        // Gold star rating badge
        JLabel ratingTag = createTag("★ 4.8", new Color(254, 249, 231), new Color(214, 137, 16));
        tagsRow.add(ratingTag);

        body.add(tagsRow);

        // Match reason label (populated when recommendation score is set)
        matchReasonLabel = new JLabel();
        matchReasonLabel.setFont(ThemeConstants.FONT_BOLD);
        matchReasonLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        matchReasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchReasonLabel.setVisible(false);

        body.add(Box.createRigidArea(new Dimension(0, 6)));
        body.add(matchReasonLabel);

        body.add(Box.createRigidArea(new Dimension(0, 6)));

        // Description
        String desc = (travelPackage != null) ? travelPackage.getDescription() : null;
        if (desc == null || desc.trim().isEmpty()) {
            desc = "Experience an unforgettable journey filled with guided tours, scenic wonders, and authentic local cuisine.";
        }
        JLabel descLabel = new JLabel("<html><body style='width: 240px; color: #7f8c8d; font-size: 11px;'>" + desc + "</body></html>");
        descLabel.setFont(ThemeConstants.FONT_SMALL);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(descLabel);

        return body;
    }

    /**
     * Creates the footer panel displaying pricing, discount badge, and action buttons.
     */
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 14, 0, 14));

        // Price Section (WEST)
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setOpaque(false);

        if (travelPackage != null) {
            double basePrice = travelPackage.getBasePrice();
            double finalPrice = travelPackage.getFinalPrice();
            double discount = travelPackage.getDiscountPercentage();

            if (discount > 0) {
                JPanel strikeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                strikeRow.setOpaque(false);

                JLabel strikePrice = new JLabel("<html><strike>$" + String.format("%.2f", basePrice) + "</strike></html>");
                strikePrice.setFont(ThemeConstants.FONT_SMALL);
                strikePrice.setForeground(ThemeConstants.TEXT_MUTED);
                strikeRow.add(strikePrice);

                JLabel discountBadge = new JLabel("-" + (int) discount + "%");
                discountBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
                discountBadge.setForeground(Color.WHITE);
                discountBadge.setBackground(ThemeConstants.DANGER_COLOR);
                discountBadge.setOpaque(true);
                discountBadge.setBorder(new EmptyBorder(1, 4, 1, 4));
                strikeRow.add(discountBadge);

                pricePanel.add(strikeRow);
            }

            JLabel finalPriceLabel = new JLabel("$" + String.format("%.2f", finalPrice));
            finalPriceLabel.setFont(ThemeConstants.FONT_HEADER);
            finalPriceLabel.setForeground(ThemeConstants.PRIMARY_DARK);
            pricePanel.add(finalPriceLabel);
        }

        footer.add(pricePanel, BorderLayout.WEST);

        // Buttons Section (EAST)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonsPanel.setOpaque(false);

        JButton detailsBtn = new JButton("View Itinerary");
        detailsBtn.setFont(ThemeConstants.FONT_SMALL);
        detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsBtn.setFocusPainted(false);
        detailsBtn.addActionListener(e -> {
            if (onViewDetailsListener != null) {
                onViewDetailsListener.accept(travelPackage);
            } else {
                showDefaultPackageDetailsDialog();
            }
        });
        buttonsPanel.add(detailsBtn);

        JButton bookBtn = new JButton("Book Now");
        bookBtn.setFont(ThemeConstants.FONT_SMALL);
        bookBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> {
            if (onBookNowListener != null) {
                onBookNowListener.accept(travelPackage);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Ready to book: \"" + (travelPackage != null ? travelPackage.getTitle() : "") + "\"\n" +
                        "Total: $" + String.format("%.2f", travelPackage != null ? travelPackage.getFinalPrice() : 0.0) + "\n\n" +
                        "(Booking integration hook: onBookNowListener)",
                        "Book Package",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        buttonsPanel.add(bookBtn);

        footer.add(buttonsPanel, BorderLayout.EAST);

        return footer;
    }

    /**
     * Attaches recommendation match score details to the card (Dev 4 contract).
     *
     * @param scorePercentage Match percentage (0-100)
     * @param matchSummary    Explanation string
     */
    public void setRecommendationScore(int scorePercentage, String matchSummary) {
        matchScoreLabel.setText(" " + scorePercentage + "% Match ");
        matchScoreLabel.setBackground(scorePercentage >= 80 ? ThemeConstants.ACCENT_COLOR : ThemeConstants.WARNING_COLOR);
        matchScoreLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        matchScoreLabel.setVisible(true);

        if (matchSummary != null && !matchSummary.isBlank()) {
            matchReasonLabel.setText("💡 " + matchSummary);
            matchReasonLabel.setVisible(true);
        }
        revalidate();
        repaint();
    }

    /**
     * Default modal dialog showing full package specifications when no external listener is registered.
     */
    private void showDefaultPackageDetailsDialog() {
        if (travelPackage == null) return;
        Destination dest = travelPackage.getDestination();
        String destName = (dest != null) ? dest.getName() + " (" + dest.getCountry() + ")" : "Unknown";
        String category = (dest != null) ? dest.getCategory() : "General";

        String message = String.format(
                "<html><body style='width: 300px; font-family: Segoe UI;'>" +
                "<h3 style='color: #1f618d; margin-bottom: 4px;'>%s</h3>" +
                "<p><b>Destination:</b> %s</p>" +
                "<p><b>Category:</b> %s</p>" +
                "<p><b>Duration:</b> %d Days</p>" +
                "<p><b>Season:</b> %s</p>" +
                "<p><b>Base Price:</b> $%.2f</p>" +
                "<p><b>Discount:</b> %.0f%%</p>" +
                "<p><b>Final Price:</b> <b style='color: #27ae60;'>$%.2f</b></p><br>" +
                "<p><b>Overview:</b><br>%s</p>" +
                "</body></html>",
                travelPackage.getTitle(),
                destName,
                category,
                travelPackage.getDurationDays(),
                travelPackage.getSeason() != null ? travelPackage.getSeason() : "All Season",
                travelPackage.getBasePrice(),
                travelPackage.getDiscountPercentage(),
                travelPackage.getFinalPrice(),
                travelPackage.getDescription() != null ? travelPackage.getDescription() : "No additional description available."
        );

        JOptionPane.showMessageDialog(
                this,
                new JLabel(message),
                "Package Details - " + travelPackage.getTitle(),
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private JLabel createTag(String text, Color bg, Color fg) {
        JLabel tag = new JLabel(text);
        tag.setFont(ThemeConstants.FONT_SMALL);
        tag.setOpaque(true);
        tag.setBackground(bg);
        tag.setForeground(fg);
        tag.setBorder(new CompoundBorder(
                new LineBorder(new Color(Math.max(0, bg.getRed() - 15), Math.max(0, bg.getGreen() - 15), Math.max(0, bg.getBlue() - 15)), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return tag;
    }

    private JLabel createCategoryPill(String category) {
        Color bg = new Color(240, 243, 246);
        Color fg = ThemeConstants.TEXT_PRIMARY;

        if ("Beach".equalsIgnoreCase(category)) {
            bg = new Color(224, 247, 250);
            fg = new Color(0, 131, 143);
        } else if ("Adventure".equalsIgnoreCase(category)) {
            bg = new Color(255, 243, 224);
            fg = new Color(230, 81, 0);
        } else if ("Culture".equalsIgnoreCase(category)) {
            bg = new Color(243, 229, 245);
            fg = new Color(106, 27, 154);
        } else if ("Nature".equalsIgnoreCase(category)) {
            bg = new Color(232, 245, 233);
            fg = new Color(46, 125, 50);
        } else if ("Luxury".equalsIgnoreCase(category)) {
            bg = new Color(254, 249, 231);
            fg = new Color(183, 149, 11);
        }

        JLabel pill = new JLabel(" " + (category != null ? category.toUpperCase() : "TOUR") + " ");
        pill.setFont(ThemeConstants.FONT_SMALL);
        pill.setForeground(fg);
        pill.setOpaque(true);
        pill.setBackground(bg);
        pill.setBorder(new CompoundBorder(
                new LineBorder(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 90), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return pill;
    }

    public Package getPackage() {
        return travelPackage;
    }

    public Package getTravelPackage() {
        return travelPackage;
    }

    public void setOnViewDetailsListener(Consumer<Package> listener) {
        this.onViewDetailsListener = listener;
    }

    public void setOnBookNowListener(Consumer<Package> listener) {
        this.onBookNowListener = listener;
    }
}
