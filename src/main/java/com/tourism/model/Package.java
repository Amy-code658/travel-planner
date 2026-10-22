package com.tourism.model;

/**
 * Travel Package entity representing curated trips.
 */
public class Package {
    private int packageId;
    private int destinationId;
    private String title;
    private String description;
    private int durationDays;
    private double basePrice;
    private String season;
    private double discountPercentage;
    private boolean active;

    // Optional joined destination for UI display convenience
    private Destination destination;

    // Associated Hotel and Transportation options (Class Diagram composition)
    private Hotel hotel;
    private Transportation transportation;

    public Package() {}

    public Package(int packageId, int destinationId, String title, String description, int durationDays, double basePrice, String season, double discountPercentage, boolean active) {
        this.packageId = packageId;
        this.destinationId = destinationId;
        this.title = title;
        this.description = description;
        this.durationDays = durationDays;
        this.basePrice = basePrice;
        this.season = season;
        this.discountPercentage = discountPercentage;
        this.active = active;
    }

    public Package(int destinationId, String title, String description, int durationDays, double basePrice, String season, double discountPercentage, boolean active) {
        this.destinationId = destinationId;
        this.title = title;
        this.description = description;
        this.durationDays = durationDays;
        this.basePrice = basePrice;
        this.season = season;
        this.discountPercentage = discountPercentage;
        this.active = active;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    /**
     * Calculates the price after discount.
     */
    public double getFinalPrice() {
        if (discountPercentage <= 0) {
            return basePrice;
        }
        return Math.round((basePrice - (basePrice * (discountPercentage / 100.0))) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return title + " (" + durationDays + " Days) - $" + getFinalPrice();
    }
}
