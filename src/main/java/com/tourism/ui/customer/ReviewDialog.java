package com.tourism.ui.customer;

import com.tourism.config.ThemeConstants;
import com.tourism.dao.ReviewDAO;
import com.tourism.model.Review;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ReviewDialog extends JDialog {

    private final int userId;
    private final int packageId;

    private final JComboBox<Integer> ratingBox;
    private final JTextArea commentArea;
    private boolean submitted = false;

    public ReviewDialog(Frame owner, int userId,
                        int packageId, String packageTitle) {

        super(owner, "Review: " + packageTitle, true);

        this.userId = userId;
        this.packageId = packageId;

        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new BorderLayout(10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Rate your experience");
        heading.setFont(ThemeConstants.FONT_HEADER);
        form.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new BorderLayout(8, 8));

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new JLabel("Rating (1-5):"));

        ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingBox.setSelectedItem(5);
        ratingPanel.add(ratingBox);

        fields.add(ratingPanel, BorderLayout.NORTH);

        commentArea = new JTextArea(6, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setFont(ThemeConstants.FONT_BODY);

        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.add(new JLabel("Your review:"), BorderLayout.NORTH);
        commentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        fields.add(commentPanel, BorderLayout.CENTER);
        form.add(fields, BorderLayout.CENTER);

        add(form, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit Review");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> submitReview());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(submitButton);

        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(submitButton);

        pack();
        setMinimumSize(new Dimension(420, 320));
        setLocationRelativeTo(owner);
    }

    private void submitReview() {

        String comment = commentArea.getText().trim();
        int rating = (Integer) ratingBox.getSelectedItem();

        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please write a review before submitting.");
            return;
        }

        Review review = new Review(userId, packageId, rating, comment);

        try {
            ReviewDAO reviewDAO = new ReviewDAO();
            reviewDAO.addReview(review);

            submitted = true;

            JOptionPane.showMessageDialog(this,
                    "Your review was submitted successfully!");

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save review: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}