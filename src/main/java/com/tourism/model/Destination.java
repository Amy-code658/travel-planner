package com.tourism.model;

/**
 * Destination entity representing travel spots.
 */
public class Destination {
    private int destinationId;
    private String name;
    private String country;
    private String description;
    private String category; // "Adventure", "Beach", "Culture", "Nature", "Luxury"
    private double estimatedDailyCost;
    private String imageUrl;
    private double averageRating;
    private int reviewCount;

    public Destination() {}

    public Destination(int destinationId, String name, String country, String description, String category, double estimatedDailyCost, String imageUrl) {
        this.destinationId = destinationId;
        this.name = name;
        this.country = country;
        this.description = description;
        this.category = category;
        this.estimatedDailyCost = estimatedDailyCost;
        this.imageUrl = imageUrl;
    }

    public Destination(String name, String country, String description, String category, double estimatedDailyCost, String imageUrl) {
        this.name = name;
        this.country = country;
        this.description = description;
        this.category = category;
        this.estimatedDailyCost = estimatedDailyCost;
        this.imageUrl = imageUrl;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getEstimatedDailyCost() {
        return estimatedDailyCost;
    }

    public void setEstimatedDailyCost(double estimatedDailyCost) {
        this.estimatedDailyCost = estimatedDailyCost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    @Override
    public String toString() {
        return name + ", " + country + " (" + category + ")";
    }
}
