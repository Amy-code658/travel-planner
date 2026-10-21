package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.Review;
import com.tourism.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for customer reviews.
 */
public class ReviewDAO {

    /**
     * Saves a new review and returns its generated ID.
     */
    public int addReview(Review review) throws SQLException {

        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }

        String sql = "INSERT INTO reviews "
                + "(user_id, package_id, rating, comment, review_date) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection connection =
                DBConnection.getInstance().getConnection();

        if (connection == null) {
            throw new SQLException("Database connection is unavailable.");
        }

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, review.getUserId());
            statement.setInt(2, review.getPackageId());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getComment());

            LocalDateTime reviewDate = review.getReviewDate();

            if (reviewDate == null) {
                reviewDate = LocalDateTime.now();
            }

            statement.setTimestamp(
                    5, Timestamp.valueOf(reviewDate)
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int reviewId = keys.getInt(1);
                    review.setReviewId(reviewId);
                    return reviewId;
                }
            }
        }

        throw new SQLException("Could not save the review.");
    }

    /**
     * Retrieves all reviews for a package, newest first.
     */
    public List<Review> getReviewsByPackageId(int packageId)
            throws SQLException {

        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT r.review_id, r.user_id, r.package_id, "
                + "r.rating, r.comment, r.review_date, "
                + "u.full_name "
                + "FROM reviews r "
                + "JOIN users u ON r.user_id = u.user_id "
                + "WHERE r.package_id = ? "
                + "ORDER BY r.review_date DESC";

        Connection connection =
                DBConnection.getInstance().getConnection();

        if (connection == null) {
            throw new SQLException("Database connection is unavailable.");
        }

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, packageId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    Review review = new Review();

                    review.setReviewId(
                            resultSet.getInt("review_id")
                    );

                    review.setUserId(
                            resultSet.getInt("user_id")
                    );

                    review.setPackageId(
                            resultSet.getInt("package_id")
                    );

                    review.setRating(
                            resultSet.getInt("rating")
                    );

                    review.setComment(
                            resultSet.getString("comment")
                    );

                    Timestamp timestamp =
                            resultSet.getTimestamp("review_date");

                    if (timestamp != null) {
                        review.setReviewDate(
                                timestamp.toLocalDateTime()
                        );
                    }

                    // Store the reviewer's name for display in the UI.
                    User user = new User();
                    user.setUserId(review.getUserId());
                    user.setFullName(
                            resultSet.getString("full_name")
                    );

                    review.setUser(user);

                    reviews.add(review);
                }
            }
        }

        return reviews;
    }

    /**
     * Calculates the average rating for a package.
     */
    public double getAverageRating(int packageId)
            throws SQLException {

        String sql = "SELECT AVG(rating) AS average_rating "
                + "FROM reviews WHERE package_id = ?";

        Connection connection =
                DBConnection.getInstance().getConnection();

        if (connection == null) {
            throw new SQLException("Database connection is unavailable.");
        }

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, packageId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getDouble("average_rating");
                }
            }
        }

        return 0.0;
    }
}