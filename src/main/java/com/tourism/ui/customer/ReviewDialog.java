package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.ReviewDAO;
import com.tourism.model.Review;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern modal dialog allowing customers to view all reviews and ratings for a place/package,
 * and submit their own rating and review to dynamically update the place's rating.
 */
public class ReviewDialog extends JDialog {

    private static final DateTimeFormatter REVIEW_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final int userId;
    private final int packageId;
    private final String packageTitle;
    private final ReviewDAO reviewDAO;
    private final Runnable onReviewSubmitted;

    private boolean submitted = false;
    private int selectedRating = 5;

    // UI components
    private JTabbedPane tabbedPane;
    private JPanel reviewsListPanel;
    private JLabel avgRatingBadge;
    private JLabel reviewCountBadge;
    private JTextArea commentArea;
    private JLabel starDescriptionLabel;
    private JButton[] starButtons;

    public ReviewDialog(Window owner, int userId, int packageId, String packageTitle, Runnable onReviewSubmitted) {
        super(owner, "Reviews & Ratings — " + packageTitle, ModalityType.APPLICATION_MODAL);
        this.userId = userId;
        this.packageId = packageId;
        this.packageTitle = packageTitle;
        this.reviewDAO = new ReviewDAO();
        this.onReviewSubmitted = onReviewSubmitted;

        initUI();
        loadReviewsAndRating();
    }

    public ReviewDialog(Frame owner, int userId, int packageId, String packageTitle) {
        this((Window) owner, userId, packageId, packageTitle, null);
    }

    private void initUI() {
        setSize(620, 600);
        setMinimumSize(new Dimension(540, 520));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        // 1. Top Header Banner with Overall Place Rating Summary
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Pane: All Reviews vs Write Review
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeConstants.FONT_BOLD);

        JPanel allReviewsTab = createAllReviewsTab();
        JPanel writeReviewTab = createWriteReviewTab();

        tabbedPane.addTab("  ⭐ All Customer Reviews  ", allReviewsTab);
        tabbedPane.addTab("  ✍️ Rate & Review Place  ", writeReviewTab);

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Bottom Close Button Bar
        JPanel footerBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footerBar.setBackground(Color.WHITE);
        footerBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(ThemeConstants.FONT_BODY);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        footerBar.add(closeBtn);

        add(footerBar, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(ThemeConstants.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("Place Ratings & Traveler Reviews");
        titleLabel.setFont(ThemeConstants.FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("📍 " + packageTitle);
        subLabel.setFont(ThemeConstants.FONT_SUBHEADER);
        subLabel.setForeground(new Color(220, 235, 252));

        titleBlock.add(titleLabel);
        titleBlock.add(subLabel);
        header.add(titleBlock, BorderLayout.WEST);

        // Rating Summary Pill Badge
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        scorePanel.setOpaque(false);

        avgRatingBadge = new JLabel("★ --");
        avgRatingBadge.setFont(new Font("Segoe UI", Font.BOLD, 22));
        avgRatingBadge.setForeground(new Color(253, 224, 71)); // Soft gold

        reviewCountBadge = new JLabel("Loading ratings...");
        reviewCountBadge.setFont(ThemeConstants.FONT_SMALL);
        reviewCountBadge.setForeground(new Color(226, 232, 240));

        JPanel badgeWrapper = new JPanel(new GridLayout(2, 1, 0, 2));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(avgRatingBadge);
        badgeWrapper.add(reviewCountBadge);

        scorePanel.add(badgeWrapper);
        header.add(scorePanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createAllReviewsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        reviewsListPanel = new JPanel();
        reviewsListPanel.setLayout(new BoxLayout(reviewsListPanel, BoxLayout.Y_AXIS));
        reviewsListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(reviewsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton leaveReviewBtn = new JButton("✍️ Leave a Review & Rate This Place");
        leaveReviewBtn.setFont(ThemeConstants.FONT_BOLD);
        leaveReviewBtn.setBackground(ThemeConstants.PRIMARY_COLOR);
        leaveReviewBtn.setForeground(Color.WHITE);
        leaveReviewBtn.setFocusPainted(false);
        leaveReviewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveReviewBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        JPanel bottomBtnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBtnRow.setOpaque(false);
        bottomBtnRow.add(leaveReviewBtn);
        panel.add(bottomBtnRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createWriteReviewTab() {
        JPanel form = new JPanel(new BorderLayout(10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel heading = new JLabel("Rate your experience at this place");
        heading.setFont(ThemeConstants.FONT_HEADER);
        heading.setForeground(ThemeConstants.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(heading);

        JLabel instruction = new JLabel("Your rating directly affects the place's overall score visible to all customers.");
        instruction.setFont(ThemeConstants.FONT_SMALL);
        instruction.setForeground(ThemeConstants.TEXT_MUTED);
        instruction.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(instruction);

        content.add(Box.createVerticalStrut(14));

        // Interactive Star Rating Picker
        JPanel starRatingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        starRatingPanel.setOpaque(false);
        starRatingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rateLabel = new JLabel("Your Rating: ");
        rateLabel.setFont(ThemeConstants.FONT_BOLD);
        starRatingPanel.add(rateLabel);

        starButtons = new JButton[5];
        selectedRating = 5;

        for (int i = 0; i < 5; i++) {
            final int starVal = i + 1;
            JButton starBtn = new JButton("★");
            starBtn.setFont(new Font("Segoe UI", Font.BOLD, 28));
            starBtn.setForeground(new Color(245, 158, 11)); // Amber
            starBtn.setBorderPainted(false);
            starBtn.setContentAreaFilled(false);
            starBtn.setFocusPainted(false);
            starBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            starBtn.setMargin(new Insets(0, 2, 0, 2));

            starBtn.addActionListener(e -> updateStarSelection(starVal));
            starButtons[i] = starBtn;
            starRatingPanel.add(starBtn);
        }

        starDescriptionLabel = new JLabel("  (5 Stars - Exceptional)");
        starDescriptionLabel.setFont(ThemeConstants.FONT_BOLD);
        starDescriptionLabel.setForeground(new Color(217, 119, 6));
        starRatingPanel.add(starDescriptionLabel);

        content.add(starRatingPanel);
        content.add(Box.createVerticalStrut(14));

        // Comment Input Area
        JLabel commentLabel = new JLabel("Write Your Review:");
        commentLabel.setFont(ThemeConstants.FONT_BOLD);
        commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(commentLabel);

        content.add(Box.createVerticalStrut(6));

        commentArea = new JTextArea(7, 30);
        commentArea.setFont(ThemeConstants.FONT_BODY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(new LineBorder(new Color(209, 213, 219), 1, true));
        commentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(commentScroll);

        form.add(content, BorderLayout.CENTER);

        // Submit Button Row
        JPanel submitRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        submitRow.setOpaque(false);

        JButton submitButton = new JButton("Submit Review & Rating");
        submitButton.setFont(ThemeConstants.FONT_BOLD);
        submitButton.setBackground(ThemeConstants.PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> submitReview());

        submitRow.add(submitButton);
        form.add(submitRow, BorderLayout.SOUTH);

        return form;
    }

    private void updateStarSelection(int starVal) {
        this.selectedRating = starVal;
        for (int j = 0; j < 5; j++) {
            if (j < starVal) {
                starButtons[j].setText("★");
                starButtons[j].setForeground(new Color(245, 158, 11));
            } else {
                starButtons[j].setText("☆");
                starButtons[j].setForeground(new Color(209, 213, 219));
            }
        }

        String desc = switch (starVal) {
            case 5 -> "  (5 Stars - Exceptional)";
            case 4 -> "  (4 Stars - Very Good)";
            case 3 -> "  (3 Stars - Average)";
            case 2 -> "  (2 Stars - Poor)";
            default -> "  (1 Star - Terrible)";
        };
        starDescriptionLabel.setText(desc);
    }

    /**
     * Loads all reviews for the current package and recalculates the average rating.
     */
    public void loadReviewsAndRating() {
        reviewsListPanel.removeAll();

        List<Review> reviews = new ArrayList<>();
        double avg = 0.0;
        int count = 0;

        try {
            reviews = reviewDAO.getReviewsByPackageId(packageId);
            avg = reviewDAO.getAverageRating(packageId);
            count = reviewDAO.getReviewCount(packageId);
        } catch (SQLException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
        }

        if (count > 0) {
            avgRatingBadge.setText(String.format("★ %.1f / 5.0", avg));
            reviewCountBadge.setText("Based on " + count + " traveler rating" + (count > 1 ? "s" : ""));
        } else {
            avgRatingBadge.setText("★ New");
            reviewCountBadge.setText("No reviews yet. Be the first to rate!");
        }

        if (reviews.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

            JLabel emptyIcon = new JLabel("🧭", SwingConstants.CENTER);
            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyMsg = new JLabel("No customer reviews yet for this destination.", SwingConstants.CENTER);
            emptyMsg.setFont(ThemeConstants.FONT_SUBHEADER);
            emptyMsg.setForeground(ThemeConstants.TEXT_PRIMARY);
            emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptySub = new JLabel("Share your travel thoughts and help fellow tourists plan their journey!", SwingConstants.CENTER);
            emptySub.setFont(ThemeConstants.FONT_BODY);
            emptySub.setForeground(ThemeConstants.TEXT_MUTED);
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptyMsg);
            emptyPanel.add(Box.createVerticalStrut(4));
            emptyPanel.add(emptySub);

            reviewsListPanel.add(emptyPanel);
        } else {
            for (Review r : reviews) {
                reviewsListPanel.add(createReviewCard(r));
                reviewsListPanel.add(Box.createVerticalStrut(10));
            }
        }

        reviewsListPanel.revalidate();
        reviewsListPanel.repaint();
    }

    private JPanel createReviewCard(Review r) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Header: User Name + Stars + Date
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        String reviewerName = (r.getUser() != null && r.getUser().getFullName() != null)
                ? r.getUser().getFullName()
                : "Verified Traveler";

        JLabel nameLabel = new JLabel("👤 " + reviewerName);
        nameLabel.setFont(ThemeConstants.FONT_BOLD);
        nameLabel.setForeground(ThemeConstants.TEXT_PRIMARY);

        JPanel starsAndDate = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        starsAndDate.setOpaque(false);

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < r.getRating() ? "★" : "☆");
        }
        JLabel starsLabel = new JLabel(stars.toString());
        starsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        starsLabel.setForeground(new Color(245, 158, 11));

        String dateStr = (r.getReviewDate() != null) ? r.getReviewDate().format(REVIEW_DATE_FORMAT) : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(ThemeConstants.FONT_SMALL);
        dateLabel.setForeground(ThemeConstants.TEXT_MUTED);

        starsAndDate.add(starsLabel);
        starsAndDate.add(dateLabel);

        cardHeader.add(nameLabel, BorderLayout.WEST);
        cardHeader.add(starsAndDate, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Comment Body
        JTextArea commentDisplay = new JTextArea(r.getComment() != null ? r.getComment() : "");
        commentDisplay.setFont(ThemeConstants.FONT_BODY);
        commentDisplay.setForeground(ThemeConstants.TEXT_PRIMARY);
        commentDisplay.setLineWrap(true);
        commentDisplay.setWrapStyleWord(true);
        commentDisplay.setEditable(false);
        commentDisplay.setOpaque(false);
        commentDisplay.setBorder(new EmptyBorder(4, 0, 2, 0));

        card.add(commentDisplay, BorderLayout.CENTER);
        return card;
    }

    private void submitReview() {
        String comment = commentArea.getText().trim();

        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please write a comment sharing your experience before submitting.",
                    "Review Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Review review = new Review(userId, packageId, selectedRating, comment);

        try {
            reviewDAO.addReview(review);
            submitted = true;

            // Recalculate average rating
            double updatedAvg = reviewDAO.getAverageRating(packageId);
            int updatedCount = reviewDAO.getReviewCount(packageId);

            JOptionPane.showMessageDialog(this,
                    String.format("Thank you! Your review has been published.%n"
                                + "New place rating: ★ %.1f/5 (%d total reviews).", updatedAvg, updatedCount),
                    "Review Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

            commentArea.setText("");
            loadReviewsAndRating();
            tabbedPane.setSelectedIndex(0); // Switch to All Reviews

            if (onReviewSubmitted != null) {
                onReviewSubmitted.run();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save review: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Focuses the dialog directly on the Write Review tab.
     */
    public void openWriteReviewTab() {
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(1);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
