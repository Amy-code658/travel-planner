package com.tourism.model;

import java.time.LocalDateTime;

/**
 * Review entity representing user ratings and comments on a package.
 */
public class Review {
    private int reviewId;
    private int userId;
    private int packageId;
    private int rating; // 1 to 5
    private String comment;
    private LocalDateTime reviewDate;

    // Optional user object to show reviewer name on UI
    private User user;

    public Review() {}

    public Review(int reviewId, int userId, int packageId, int rating, String comment, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.packageId = packageId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public Review(int userId, int packageId, int rating, String comment) {
        this.userId = userId;
        this.packageId = packageId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Review{" + "rating=" + rating + ", comment='" + comment + '\'' + '}';
    }
}
