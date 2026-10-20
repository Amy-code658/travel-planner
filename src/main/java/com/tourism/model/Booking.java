package com.tourism.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking entity representing a customer trip reservation.
 */
public class Booking {
    // Possible values of booking_status (used by Dev 5's booking module)
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";
    public static final String COMPLETED = "COMPLETED";

    private int bookingId;
    private int userId;
    private int packageId;
    private LocalDate travelDate;
    private int numberOfGuests;
    private double totalCost;
    private String bookingStatus; // "CONFIRMED", "CANCELLED", "COMPLETED"
    private double refundAmount;
    private LocalDateTime bookingDate;

    // Optional joined objects for UI and PDF reporting
    private Package travelPackage;
    private User user;

    // Package title from a JOIN query, used by the booking history table
    private String packageTitle;

    public Booking() {}

    public Booking(int bookingId, int userId, int packageId, LocalDate travelDate, int numberOfGuests, double totalCost, String bookingStatus, double refundAmount, LocalDateTime bookingDate) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.numberOfGuests = numberOfGuests;
        this.totalCost = totalCost;
        this.bookingStatus = bookingStatus;
        this.refundAmount = refundAmount;
        this.bookingDate = bookingDate;
    }

    public Booking(int userId, int packageId, LocalDate travelDate, int numberOfGuests, double totalCost) {
        this.userId = userId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.numberOfGuests = numberOfGuests;
        this.totalCost = totalCost;
        this.bookingStatus = "CONFIRMED";
        this.refundAmount = 0.0;
        this.bookingDate = LocalDateTime.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Package getTravelPackage() {
        return travelPackage;
    }

    public void setTravelPackage(Package travelPackage) {
        this.travelPackage = travelPackage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPackageTitle() {
        return packageTitle;
    }

    public void setPackageTitle(String packageTitle) {
        this.packageTitle = packageTitle;
    }

    // Short aliases for bookingStatus, used by BookingDAO and BookingService
    public String getStatus() {
        return bookingStatus;
    }

    public void setStatus(String status) {
        this.bookingStatus = status;
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(bookingStatus);
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " [Status: " + bookingStatus + ", Total: $" + totalCost + "]";
    }
}
