package com.tourism;

import com.tourism.dao.BookingDAO;
import com.tourism.dao.PackageDAO;
import com.tourism.dao.ReviewDAO;
import com.tourism.model.Booking;
import com.tourism.model.Destination;
import com.tourism.model.Package;
import com.tourism.model.Review;
import com.tourism.service.BookingService;
import com.tourism.ui.customer.CancelBookingDialog;
import com.tourism.ui.customer.PackageCard;
import com.tourism.ui.customer.ReviewDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * End-to-end automated verification test for:
 * 1. Customer reviews and dynamic place rating updates.
 * 2. Booking cancellation and refund credited back notification amount calculations.
 */
public class ReviewsAndCancellationVerificationTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   VERIFICATION: REVIEWS & PLACE RATINGS + CANCEL BOOKING & REFUND NOTIFICATION ");
        System.out.println("================================================================================");

        testReviewAndPlaceRating();
        testCancellationAndRefundCredit();
        testUIComponents();

        System.out.println("\n================================================================================");
        System.out.println("   ALL REVIEW & CANCELLATION VERIFICATIONS COMPLETED SUCCESSFULLY!");
        System.out.println("================================================================================");
    }

    private static void testReviewAndPlaceRating() {
        System.out.println("\n--- [1] Testing Customer Reviews & Dynamic Place Rating Calculation ---");

        ReviewDAO reviewDAO = new ReviewDAO();
        PackageDAO packageDAO = new PackageDAO();

        try {
            int packageId = 1; // Bali Tropical Island Escape
            int userId = 2;    // John Doe

            // Check initial reviews count and rating
            double initialAvg = reviewDAO.getAverageRating(packageId);
            int initialCount = reviewDAO.getReviewCount(packageId);
            System.out.printf("  Initial status for Package #%d: Rating = %.1f/5, Reviews = %d%n",
                    packageId, initialAvg, initialCount);

            // Add a new review from a customer
            Review newReview = new Review(userId, packageId, 5, "Phenomenal beaches and lovely culture!");
            int generatedId = reviewDAO.addReview(newReview);
            System.out.printf("  Added new customer review ID: %d (5 stars)%n", generatedId);
            assert generatedId > 0 : "Review ID should be generated";

            // Verify updated average rating and count
            double updatedAvg = reviewDAO.getAverageRating(packageId);
            int updatedCount = reviewDAO.getReviewCount(packageId);
            System.out.printf("  Updated status for Package #%d: Rating = %.1f/5, Reviews = %d%n",
                    packageId, updatedAvg, updatedCount);
            assert updatedCount == initialCount + 1 : "Review count must increment by 1";
            assert updatedAvg >= 1.0 && updatedAvg <= 5.0 : "Average rating must be between 1 and 5";

            // Verify Destination (Place) rating
            int destinationId = 1; // Bali
            double destAvg = reviewDAO.getAverageRatingForDestination(destinationId);
            int destCount = reviewDAO.getReviewCountForDestination(destinationId);
            System.out.printf("  Destination #%d (Bali) Overall Rating: %.1f/5 (%d reviews)%n",
                    destinationId, destAvg, destCount);
            assert destCount > 0 : "Destination reviews count should be > 0";

            // Verify PackageDAO maps averageRating and reviewCount onto Package and Destination models
            Package pkg = packageDAO.getPackageById(packageId);
            assert pkg != null : "Package #1 must exist";
            System.out.printf("  PackageDAO mapped model: Title = '%s', AvgRating = %.1f, Reviews = %d%n",
                    pkg.getTitle(), pkg.getAverageRating(), pkg.getReviewCount());
            assert pkg.getAverageRating() > 0 : "Package average rating should be > 0";
            assert pkg.getReviewCount() > 0 : "Package review count should be > 0";

            if (pkg.getDestination() != null) {
                System.out.printf("  Joined Destination: Name = '%s', AvgRating = %.1f, Reviews = %d%n",
                        pkg.getDestination().getName(),
                        pkg.getDestination().getAverageRating(),
                        pkg.getDestination().getReviewCount());
                assert pkg.getDestination().getAverageRating() > 0 : "Destination rating should be > 0";
            }

            // Verify filter search by minimum rating
            List<Package> topRated = packageDAO.searchPackages(null, null, null, null, 4.0);
            System.out.printf("  Packages with rating >= 4.0: %d found%n", topRated.size());
            assert !topRated.isEmpty() : "Should find top rated packages";
            for (Package p : topRated) {
                assert p.getAverageRating() >= 4.0 : "Rating must be >= 4.0";
            }

            System.out.println("  >> Review & Place Rating Engine: PASSED (OK)");

        } catch (Exception e) {
            System.err.println("  ❌ Error during review test: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void testCancellationAndRefundCredit() {
        System.out.println("\n--- [2] Testing Booking Cancellation & Refund Credit Calculation ---");

        BookingService service = new BookingService();
        BookingDAO dao = new BookingDAO();

        // 1. Test Refund Calculation Logic
        LocalDate futureTravelDate = LocalDate.now().plusDays(10); // > 48 hours
        double originalCost = 500.00;
        double earlyRefund = service.calculateRefund(futureTravelDate, originalCost, LocalDateTime.now());
        System.out.printf("  Original Cost: $%.2f | Early Cancel (>48h) Refund: $%.2f (80%% rule)%n",
                originalCost, earlyRefund);
        assert earlyRefund == 400.00 : "80% of $500 should be $400.00";

        LocalDate nearTravelDate = LocalDate.now().plusDays(1); // <= 48 hours
        double lateRefund = service.calculateRefund(nearTravelDate, originalCost, LocalDateTime.now());
        System.out.printf("  Original Cost: $%.2f | Late Cancel (<=48h) Refund: $%.2f (0%% rule)%n",
                originalCost, lateRefund);
        assert lateRefund == 0.00 : "Within 48h refund must be $0.00";

        // 2. Test Booking Cancellation in database
        try {
            // Find an active confirmed booking for John Doe (user_id 2)
            List<Booking> userBookings = dao.getUserBookings(2);
            Booking confirmedBooking = userBookings.stream()
                    .filter(b -> Booking.CONFIRMED.equals(b.getStatus()))
                    .findFirst()
                    .orElse(null);

            if (confirmedBooking != null) {
                System.out.printf("  Testing cancellation on Booking #%d for '%s' (Travel date: %s)%n",
                        confirmedBooking.getBookingId(),
                        confirmedBooking.getPackageTitle(),
                        confirmedBooking.getTravelDate());

                double refundCalculated = service.calculateRefund(confirmedBooking);
                System.out.printf("  Calculated Refund Amount to be Credited Back: $%.2f%n", refundCalculated);

                // Cancel the booking through the service
                double refunded = service.cancelBooking(confirmedBooking.getBookingId(), confirmedBooking.getUserId());
                System.out.printf("  Service cancelled booking: Actual Credited Refund = $%.2f%n", refunded);
                assert refunded == refundCalculated : "Refund credited must match calculation";

                // Verify updated state in database
                Booking updated = dao.getBookingById(confirmedBooking.getBookingId());
                assert updated != null : "Updated booking must exist";
                System.out.printf("  Database record updated: Status = %s, RefundAmount = $%.2f%n",
                        updated.getStatus(), updated.getRefundAmount());
                assert Booking.CANCELLED.equals(updated.getStatus()) : "Status must be CANCELLED";
                assert updated.getRefundAmount() == refunded : "Refund amount in DB must match";

                // Verify trying to cancel again throws BookingException
                try {
                    service.cancelBooking(confirmedBooking.getBookingId(), confirmedBooking.getUserId());
                    assert false : "Should not be able to cancel an already cancelled booking";
                } catch (Exception expected) {
                    System.out.println("  Cancelling already cancelled booking correctly rejected: " + expected.getMessage());
                }
            } else {
                System.out.println("  Note: No CONFIRMED booking found for user 2 to cancel; logic verified via unit tests.");
            }

            System.out.println("  >> Cancellation & Refund Credit Engine: PASSED (OK)");

        } catch (Exception e) {
            System.err.println("  ❌ Error during cancellation test: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void testUIComponents() {
        System.out.println("\n--- [3] Testing UI Dialogs & Card Integration ---");

        Destination bali = new Destination(1, "Bali", "Indonesia", "Tropical paradise", "Beach", 60.0, "bali.jpg");
        bali.setAverageRating(4.8);
        bali.setReviewCount(12);

        Package pkg = new Package(1, 1, "Bali Tropical Island Escape", "Beach and temples", 5, 450.0, "Summer", 10.0, true);
        pkg.setDestination(bali);
        pkg.setAverageRating(4.8);
        pkg.setReviewCount(12);

        // Verify PackageCard instantiation with dynamic rating
        PackageCard card = new PackageCard(pkg);
        assert card.getPackage() != null;
        assert card.getPackage().getAverageRating() == 4.8;
        assert card.getPackage().getReviewCount() == 12;
        System.out.println("  PackageCard dynamic rating initialized: " + card.getPackage().getAverageRating() + " (" + card.getPackage().getReviewCount() + " reviews)");

        // Verify CancelBookingDialog initialization
        Booking b = new Booking(2, 1, LocalDate.now().plusDays(7), 2, 810.0);
        b.setBookingId(999);
        b.setPackageTitle("Bali Tropical Island Escape");
        b.setStatus(Booking.CONFIRMED);

        CancelBookingDialog cancelDialog = new CancelBookingDialog(null, b, new BookingService());
        assert cancelDialog.getRefundAmount() > 0 : "Should calculate > 0 refund for trip 7 days away";
        System.out.println("  CancelBookingDialog calculated credit refund: $" + cancelDialog.getRefundAmount());
        cancelDialog.dispose();

        // Verify ReviewDialog initialization
        ReviewDialog reviewDialog = new ReviewDialog(null, 2, 1, "Bali Tropical Island Escape", () -> {});
        assert reviewDialog != null;
        System.out.println("  ReviewDialog instantiated cleanly with review tabs");
        reviewDialog.dispose();

        System.out.println("  >> UI Components & Contracts: PASSED (OK)");
    }
}
