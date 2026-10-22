package com.tourism.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Composite model representing a customer's sign-in identity, chosen package,
 * travel schedule, and payment fulfillment status for the Admin Console.
 */
public class CustomerBookingDossier {

    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime userCreatedAt;

    private Integer bookingId; // Null if customer has only signed in / registered without booking yet
    private Integer packageId;
    private String packageTitle;
    private String destinationName;
    private String destinationCountry;
    private String destinationCategory;
    private Integer durationDays;
    private LocalDate travelDate;
    private Integer numberOfGuests;
    private String bookingStatus; // CONFIRMED, CANCELLED, COMPLETED, or null
    private String paymentStatus; // PAID, NOT PAID, PENDING, PARTIALLY PAID, REFUNDED
    private LocalDateTime bookingDate;
    private String destinationImageUrl;

    public CustomerBookingDossier() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName != null ? fullName : "Unknown Customer";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone != null && !phone.isBlank() ? phone : "No Phone Listed";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(LocalDateTime userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getPackageTitle() {
        return packageTitle != null ? packageTitle : "No Package Selected (Browsing)";
    }

    public void setPackageTitle(String packageTitle) {
        this.packageTitle = packageTitle;
    }

    public String getDestinationName() {
        return destinationName != null ? destinationName : "—";
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationCountry() {
        return destinationCountry != null ? destinationCountry : "";
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationCategory() {
        return destinationCategory != null ? destinationCategory : "General";
    }

    public void setDestinationCategory(String destinationCategory) {
        this.destinationCategory = destinationCategory;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getBookingStatus() {
        if (bookingId == null) {
            return "BROWSING";
        }
        return bookingStatus != null ? bookingStatus : "CONFIRMED";
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        if (bookingId == null) {
            return "NOT PAID";
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            return paymentStatus.toUpperCase();
        }
        if ("CANCELLED".equalsIgnoreCase(bookingStatus)) {
            return "REFUNDED";
        }
        return "PAID";
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getDestinationImageUrl() {
        return destinationImageUrl != null && !destinationImageUrl.isBlank() ? destinationImageUrl : "bali.jpg";
    }

    public void setDestinationImageUrl(String destinationImageUrl) {
        this.destinationImageUrl = destinationImageUrl;
    }

    public boolean hasBooking() {
        return bookingId != null && bookingId > 0;
    }
}
