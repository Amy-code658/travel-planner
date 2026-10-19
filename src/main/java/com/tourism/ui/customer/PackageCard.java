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
 */
public class PackageCard extends JPanel {

    private final Package travelPackage;
    private Consumer<Package> onViewDetailsListener;
    private Consumer<Package> onBookNowListener;

    public PackageCard(Package travelPackage) {
        this.travelPackage = travelPackage;
        initCardUI();
    }

    private void initCardUI() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.CARD_BG);
        setPreferredSize(new Dimension(300, 390));
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
        Destination dest = travelPackage.getDestination();
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
                    // Subtle dark gradient overlay on image for readable badges
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

        // Category pill on top-left of banner
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);

        JLabel categoryBadge = new JLabel(" " + category.toUpperCase() + " ");
        categoryBadge.setFont(ThemeConstants.FONT_SMALL);
        categoryBadge.setForeground(Color.WHITE);
        categoryBadge.setOpaque(true);
        categoryBadge.setBackground(new Color(0, 0, 0, 140));
        categoryBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 100), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));
        topRow.add(categoryBadge);

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
     * Creates the card body containing title, tags (duration, season), and description.
     */
    private JPanel createBodyPanel() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 14, 8, 14));

        // Package Title
        JLabel titleLabel = new JLabel("<html><b>" + travelPackage.getTitle() + "</b></html>");
        titleLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(titleLabel);

        body.add(Box.createRigidArea(new Dimension(0, 8)));

        // Tags Row: Duration & Season
        JPanel tagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tagsRow.setOpaque(false);
        tagsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel durationTag = createTag("⏱ " + travelPackage.getDurationDays() + " Days", new Color(240, 243, 246), ThemeConstants.TEXT_PRIMARY);
        tagsRow.add(durationTag);

        if (travelPackage.getSeason() != null && !travelPackage.getSeason().trim().isEmpty()) {
            JLabel seasonTag = createTag("☀️ " + travelPackage.getSeason(), new Color(254, 249, 231), new Color(183, 119, 0));
            tagsRow.add(seasonTag);
        }

        body.add(tagsRow);
        body.add(Box.createRigidArea(new Dimension(0, 8)));

        // Description with text wrapping
        String desc = travelPackage.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "Experience an unforgettable journey filled with guided tours, scenic wonders, and authentic local cuisine.";
        }
        JLabel descLabel = new JLabel("<html><p style='width: 210px; color: #7f8c8d; font-size: 11px;'>" + desc + "</p></html>");
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

        footer.add(pricePanel, BorderLayout.WEST);

        // Buttons Section (EAST)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonsPanel.setOpaque(false);

        JButton detailsBtn = new JButton("Details");
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

        JButton bookBtn = new JButton("Book");
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
                        "Ready to book: \"" + travelPackage.getTitle() + "\"\n" +
                        "Total: $" + String.format("%.2f", travelPackage.getFinalPrice()) + "\n\n" +
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
     * Default modal dialog showing full package specifications when no external listener is registered.
     */
    private void showDefaultPackageDetailsDialog() {
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
                new LineBorder(new Color(bg.getRed() - 15, bg.getGreen() - 15, bg.getBlue() - 15), 1, true),
                new EmptyBorder(2, 6, 2, 6)
        ));
        return tag;
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
