package com.tourism.service;

import com.tourism.dao.BookingDAO;
import com.tourism.model.Booking;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic for bookings: cost calculation, refund rule, validation.
 * The UI calls this class, and this class calls BookingDAO (UI -> Service -> DAO -> Database).
 * Concepts: constants, method overloading, exception handling, date/time API, composition.
 */
public class BookingService {

    public static final double REFUND_PERCENT = 80.0;       // refund when cancelled early
    public static final long REFUND_CUTOFF_HOURS = 48;      // "early" means more than 48 hours before the trip
    public static final int MAX_GUESTS = 20;

    private final BookingDAO bookingDAO = new BookingDAO();

    /**
     * Total = (BasePrice x Guests) x (1 - Discount).
     * discountPercent comes from the database as a percentage (10 means 10%).
     */
    public double calculateTotal(double basePrice, int guests, double discountPercent) {
        double total = basePrice * guests * (1 - discountPercent / 100.0);
        return Math.round(total * 100.0) / 100.0;           // round to 2 decimal places
    }

    /**
     * Refund rule: more than 48 hours before the trip starts gives 80% back, otherwise 0%.
     * The trip is taken to start at 00:00 on the travel date.
     * 'now' is a parameter so the rule is easy to test with any date.
     */
    public double calculateRefund(LocalDate travelDate, double totalCost, LocalDateTime now) {
        long minutesLeft = Duration.between(now, travelDate.atStartOfDay()).toMinutes();
        if (minutesLeft > REFUND_CUTOFF_HOURS * 60) {
            return Math.round(totalCost * REFUND_PERCENT) / 100.0;
        }
        return 0.0;
    }

    // Same method name, different parameters (method overloading): uses the current time
    public double calculateRefund(Booking booking) {
        return calculateRefund(booking.getTravelDate(), booking.getTotalCost(), LocalDateTime.now());
    }

    /** Checks the date, guest count and payment details. Throws BookingException if something is wrong. */
    public void validateBookingRequest(LocalDate travelDate, int guests, PaymentMethod payment)
            throws BookingException {
        if (travelDate == null || !travelDate.isAfter(LocalDate.now())) {
            throw new BookingException("Travel date must be a future date.");
        }
        if (guests < 1 || guests > MAX_GUESTS) {
            throw new BookingException("Number of guests must be between 1 and " + MAX_GUESTS + ".");
        }
        payment.validate();
    }

    /** Validates, calculates the cost and saves the booking. Returns the saved booking. */
    public Booking bookTrip(int userId, int packageId, double basePrice, double discountPercent,
                            LocalDate travelDate, int guests, PaymentMethod payment)
            throws BookingException {

        validateBookingRequest(travelDate, guests, payment);

        double total = calculateTotal(basePrice, guests, discountPercent);
        Booking booking = new Booking(userId, packageId, travelDate, guests, total);

        try {
            int newId = bookingDAO.createBooking(booking);
            booking.setBookingId(newId);
            booking.setBookingDate(LocalDateTime.now());
            return booking;
        } catch (SQLException e) {
            throw new BookingException("Booking failed: " + e.getMessage(), e);
        }
    }

    /** Cancels a booking of this user and returns the refund amount. */
    public double cancelBooking(int bookingId, int userId) throws BookingException {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null || booking.getUserId() != userId) {
                throw new BookingException("Booking not found.");
            }
            if (!Booking.CONFIRMED.equals(booking.getStatus())) {
                throw new BookingException("Only confirmed bookings can be cancelled.");
            }
            if (booking.getTravelDate().isBefore(LocalDate.now())) {
                throw new BookingException("This trip has already started, so it cannot be cancelled.");
            }

            double refund = calculateRefund(booking);
            if (!bookingDAO.cancelBooking(bookingId, refund)) {
                throw new BookingException("Could not cancel this booking.");
            }
            return refund;

        } catch (SQLException e) {
            throw new BookingException("Cancellation failed: " + e.getMessage(), e);
        }
    }

    /** All bookings of a user. Finished trips are first marked COMPLETED. */
    public List<Booking> getUserBookings(int userId) throws BookingException {
        try {
            bookingDAO.markPastTripsCompleted();
            return bookingDAO.getUserBookings(userId);
        } catch (SQLException e) {
            throw new BookingException("Could not load bookings: " + e.getMessage(), e);
        }
    }
}
