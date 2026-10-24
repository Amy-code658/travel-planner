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
    private Runnable onReviewChangedListener;

    private JLabel matchScoreLabel;
    private JLabel matchReasonLabel;
    private JLabel strikePrice;
    private JLabel finalPriceLabel;
    private JLabel ratingTag;

    public PackageCard(Package travelPackage) {
        this.travelPackage = travelPackage;
        initCardUI();
        com.tourism.config.CurrencyManager.getInstance().addListener(this::updateCurrency);
    }

    private void initCardUI() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.CARD_BG);
        setPreferredSize(new Dimension(305, 320));
        setMinimumSize(new Dimension(280, 295));

        // Modern card border with subtle outline, no wasteful bottom insets
        setBorder(new LineBorder(new Color(228, 231, 236), 1, true));

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
                        bottomColor = new Color(230, 126, 34);
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

        banner.setPreferredSize(new Dimension(295, 132));
        banner.setBorder(new EmptyBorder(8, 10, 8, 10));

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
        JPanel bottomRow = new JPanel(new BorderLayout(6, 0));
        bottomRow.setOpaque(false);

        String destLocation = (dest != null) ? dest.getName() + ", " + dest.getCountry() : "Worldwide";
        JLabel locationLabel = new JLabel("📍 " + destLocation);
        locationLabel.setFont(ThemeConstants.FONT_BOLD);
        locationLabel.setForeground(Color.WHITE);
        bottomRow.add(locationLabel, BorderLayout.WEST);

        // Live Weather & Climate Badge
        String weatherText = getWeatherBadge(dest, travelPackage);
        String weatherTip = getWeatherTip(dest, travelPackage);
        JLabel weatherBadge = new JLabel(weatherText);
        weatherBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        weatherBadge.setForeground(Color.WHITE);
        weatherBadge.setBackground(new Color(0, 0, 0, 130));
        weatherBadge.setOpaque(true);
        weatherBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        weatherBadge.setToolTipText("<html><b>Seasonal Climate:</b> " + weatherTip + "</html>");
        bottomRow.add(weatherBadge, BorderLayout.EAST);

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
        body.setBorder(new EmptyBorder(8, 12, 2, 12));

        String title = (travelPackage != null && travelPackage.getTitle() != null) ? travelPackage.getTitle() : "Travel Package";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(titleLabel);

        body.add(Box.createRigidArea(new Dimension(0, 4)));

        // Tags Row: Duration, Rating, Season
        JPanel tagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tagsRow.setOpaque(false);
        tagsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        int duration = (travelPackage != null) ? travelPackage.getDurationDays() : 0;
        JLabel durationTag = createTag("⏱ " + duration + " Days", new Color(243, 244, 246), ThemeConstants.TEXT_PRIMARY);
        tagsRow.add(durationTag);

        ratingTag = createTag("★ 5.0", new Color(254, 249, 231), new Color(183, 119, 0));
        ratingTag.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ratingTag.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openReviewsDialog();
            }
        });
        updateRatingDisplay();
        tagsRow.add(ratingTag);

        if (travelPackage != null && travelPackage.getSeason() != null && !travelPackage.getSeason().trim().isEmpty()) {
            JLabel seasonTag = createTag("☀️ " + travelPackage.getSeason(), new Color(254, 243, 237), ThemeConstants.PRIMARY_DARK);
            tagsRow.add(seasonTag);
        }

        body.add(tagsRow);

        // Match reason label (populated when recommendation score is set)
        matchReasonLabel = new JLabel();
        matchReasonLabel.setFont(ThemeConstants.FONT_BOLD);
        matchReasonLabel.setForeground(ThemeConstants.PRIMARY_DARK);
        matchReasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchReasonLabel.setVisible(false);
        body.add(matchReasonLabel);

        body.add(Box.createRigidArea(new Dimension(0, 4)));

        // Description: Compact 2-line snippet utilizing full card width
        String desc = (travelPackage != null) ? travelPackage.getDescription() : null;
        if (desc == null || desc.trim().isEmpty()) {
            desc = "Experience an unforgettable journey filled with guided tours, scenic wonders, and authentic local cuisine.";
        }
        JLabel descLabel = new JLabel("<html><body style='width: 265px; color: #64748b; font-size: 11px; line-height: 1.25;'>" + desc + "</body></html>");
        descLabel.setFont(ThemeConstants.FONT_SMALL);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(descLabel);

        return body;
    }

    /**
     * Creates the footer panel displaying pricing, discount badge, and action buttons.
     */
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(6, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(4, 12, 10, 12));

        // Price Section (WEST)
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setOpaque(false);

        if (travelPackage != null) {
            double basePrice = travelPackage.getBasePrice();
            double finalPrice = travelPackage.getFinalPrice();
            double discount = travelPackage.getDiscountPercentage();

            com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();

            if (discount > 0) {
                JPanel strikeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                strikeRow.setOpaque(false);

                strikePrice = new JLabel("<html><strike>" + cm.format(basePrice) + "</strike></html>");
                strikePrice.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                strikePrice.setForeground(ThemeConstants.TEXT_MUTED);
                strikeRow.add(strikePrice);

                JLabel discountBadge = new JLabel("-" + (int) discount + "%");
                discountBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                discountBadge.setForeground(Color.WHITE);
                discountBadge.setBackground(ThemeConstants.DANGER_COLOR);
                discountBadge.setOpaque(true);
                discountBadge.setBorder(new EmptyBorder(1, 3, 1, 3));
                strikeRow.add(discountBadge);

                pricePanel.add(strikeRow);
            }

            finalPriceLabel = new JLabel(cm.format(finalPrice));
            finalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            finalPriceLabel.setForeground(ThemeConstants.PRIMARY_DARK);
            pricePanel.add(finalPriceLabel);
        }

        footer.add(pricePanel, BorderLayout.WEST);

        // Buttons Section (EAST)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);

        JButton detailsBtn = new JButton("Details");
        detailsBtn.setFont(ThemeConstants.FONT_SMALL);
        detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsBtn.setFocusPainted(false);
        detailsBtn.setPreferredSize(new Dimension(68, 28));
        detailsBtn.addActionListener(e -> {
            if (onViewDetailsListener != null) {
                onViewDetailsListener.accept(travelPackage);
            } else {
                showDefaultPackageDetailsDialog();
            }
        });
        buttonsPanel.add(detailsBtn);

        JButton reviewsBtn = new JButton("Reviews");
        reviewsBtn.setFont(ThemeConstants.FONT_SMALL);
        reviewsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reviewsBtn.setFocusPainted(false);
        reviewsBtn.setPreferredSize(new Dimension(72, 28));
        reviewsBtn.addActionListener(e -> openReviewsDialog());
        buttonsPanel.add(reviewsBtn);

        JButton bookBtn = new JButton("Book Now");
        bookBtn.setFont(ThemeConstants.FONT_BOLD);
        bookBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.setFocusPainted(false);
        bookBtn.setPreferredSize(new Dimension(88, 28));
        bookBtn.addActionListener(e -> {
            if (onBookNowListener != null) {
                onBookNowListener.accept(travelPackage);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Ready to book: \"" + (travelPackage != null ? travelPackage.getTitle() : "") + "\"\n" +
                        "Total: " + (travelPackage != null ? com.tourism.config.CurrencyManager.getInstance().format(travelPackage.getFinalPrice()) : "") + "\n\n" +
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
     * Dynamically updates the price formatting when currency changes.
     */
    public void updateCurrency() {
        if (travelPackage != null) {
            com.tourism.config.CurrencyManager cm = com.tourism.config.CurrencyManager.getInstance();
            if (strikePrice != null) {
                strikePrice.setText("<html><strike>" + cm.format(travelPackage.getBasePrice()) + "</strike></html>");
            }
            if (finalPriceLabel != null) {
                finalPriceLabel.setText(cm.format(travelPackage.getFinalPrice()));
            }
            revalidate();
            repaint();
        }
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

    public void setOnReviewChangedListener(Runnable listener) {
        this.onReviewChangedListener = listener;
    }

    /**
     * Dynamically updates the rating tag based on customer reviews.
     */
    public void updateRatingDisplay() {
        if (ratingTag == null || travelPackage == null) return;
        double avg = travelPackage.getAverageRating();
        int count = travelPackage.getReviewCount();

        if (count > 0) {
            ratingTag.setText(String.format("★ %.1f (%d)", avg, count));
            ratingTag.setToolTipText(String.format("Rated %.1f / 5 based on %d customer reviews. Click to view or leave a review.", avg, count));
            ratingTag.setBackground(new Color(254, 249, 231));
            ratingTag.setForeground(new Color(183, 119, 0));
        } else {
            ratingTag.setText("★ Rate Place");
            ratingTag.setToolTipText("No reviews yet. Click to be the first to rate and review this place!");
            ratingTag.setBackground(new Color(243, 244, 246));
            ratingTag.setForeground(ThemeConstants.TEXT_MUTED);
        }
        ratingTag.revalidate();
        ratingTag.repaint();
    }

    /**
     * Opens the Reviews & Ratings dialog for this package/destination.
     */
    public void openReviewsDialog() {
        if (travelPackage == null) return;
        Window owner = SwingUtilities.getWindowAncestor(this);

        int currentUserId = 2; // Default John Doe
        if (com.tourism.model.UserSession.getInstance().getCurrentUser() != null) {
            currentUserId = com.tourism.model.UserSession.getInstance().getCurrentUser().getUserId();
        }

        ReviewDialog dialog = new ReviewDialog(owner, currentUserId, travelPackage.getPackageId(), travelPackage.getTitle(), () -> {
            // Live refresh of average rating and review count from database
            try {
                com.tourism.dao.ReviewDAO rDao = new com.tourism.dao.ReviewDAO();
                double newAvg = rDao.getAverageRating(travelPackage.getPackageId());
                int newCount = rDao.getReviewCount(travelPackage.getPackageId());
                travelPackage.setAverageRating(newAvg);
                travelPackage.setReviewCount(newCount);
                if (travelPackage.getDestination() != null) {
                    travelPackage.getDestination().setAverageRating(newAvg);
                    travelPackage.getDestination().setReviewCount(newCount);
                }
                updateRatingDisplay();
            } catch (Exception ignored) {}

            if (onReviewChangedListener != null) {
                onReviewChangedListener.run();
            }
        });

        dialog.setVisible(true);
    }

    private String getWeatherBadge(Destination dest, Package pkg) {
        String name = (dest != null && dest.getName() != null) ? dest.getName().toLowerCase() : "";
        if (name.contains("bali")) return "☀️ 29°C";
        if (name.contains("alps") || name.contains("swiss")) return "❄️ -2°C";
        if (name.contains("kyoto") || name.contains("japan")) return "🌸 18°C";
        if (name.contains("goa")) return "🌴 31°C";
        if (name.contains("paris") || name.contains("france")) return "⛅ 16°C";
        if (name.contains("manali") || name.contains("kullu")) return "🏔️ 8°C";
        if (name.contains("shimla")) return "🌲 14°C";
        if (name.contains("kerala")) return "🌧️ 26°C";
        if (name.contains("vietnam")) return "🏮 28°C";
        if (name.contains("maldives")) return "🏖️ 30°C";
        if (name.contains("egypt") || name.contains("cairo")) return "🏜️ 33°C";

        // Category/Season fallback
        String category = (dest != null && dest.getCategory() != null) ? dest.getCategory().toLowerCase() : "";
        if (category.contains("beach")) return "🌴 30°C";
        if (category.contains("adventure")) return "🏔️ 15°C";
        if (category.contains("culture")) return "🏛️ 21°C";
        return "☀️ 24°C";
    }

    private String getWeatherTip(Destination dest, Package pkg) {
        String name = (dest != null && dest.getName() != null) ? dest.getName().toLowerCase() : "";
        if (name.contains("bali")) return "Tropical sunshine! Pack lightweight cottons, swimwear & sunscreen.";
        if (name.contains("alps") || name.contains("swiss")) return "Sub-zero powder snow! Thermal layers, gloves & snow boots recommended.";
        if (name.contains("kyoto") || name.contains("japan")) return "Mild spring climate with blooming cherry trees. Light cardigan recommended.";
        if (name.contains("goa")) return "Warm coastal breeze! Ideal for beach walks, water sports & shades.";
        if (name.contains("paris") || name.contains("france")) return "Pleasant European breeze. Trench coats, scarves & comfortable walking shoes recommended.";
        if (name.contains("manali") || name.contains("kullu")) return "High-altitude crisp chill. Warm woolens, thermals & trekking shoes recommended.";
        if (name.contains("shimla")) return "Refreshing pine mountain weather. Light jackets & evening shawls recommended.";
        if (name.contains("kerala")) return "Lush coastal tropical climate with gentle backwater breeze. Light cottons & umbrella recommended.";
        if (name.contains("vietnam")) return "Warm tropical climate with vibrant lantern nights. Breathable fabrics & rain gear recommended.";
        if (name.contains("maldives")) return "Pristine sunny lagoon weather. Swimwear, snorkel kit & high SPF recommended.";
        if (name.contains("egypt") || name.contains("cairo")) return "Dry desert warmth around historic pyramids. Sun hats, UV protection & hydration essential.";
        return "Ideal travel conditions. Check local daily forecast before departure.";
    }
}
