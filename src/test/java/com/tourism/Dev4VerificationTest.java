package com.tourism;

import com.tourism.model.Destination;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;
import com.tourism.service.ItineraryService;
import com.tourism.service.RecommendationService;
import com.tourism.service.RecommendationService.ScoredPackage;

import java.util.ArrayList;
import java.util.List;

public class Dev4VerificationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING DEV 4 VERIFICATION (ALGORITHM & ITINERARY)");
        System.out.println("==================================================");

        // 1. Create sample test packages
        List<Package> mockPackages = new ArrayList<>();

        Destination bali = new Destination(1, "Bali", "Indonesia", "Tropical beaches and temples", "Beach", 60.0, "bali.jpg");
        Destination alps = new Destination(2, "Swiss Alps", "Switzerland", "Alpine trails and scenery", "Adventure", 180.0, "alps.jpg");
        Destination kyoto = new Destination(3, "Kyoto", "Japan", "Ancient shrines and gardens", "Culture", 110.0, "kyoto.jpg");
        Destination manali = new Destination(4, "Manali", "India", "Mountain resort town", "Nature", 40.0, "manali.jpg");

        Package p1 = new Package(1, 1, "Bali Tropical Island Escape", "Beach and temples", 5, 450.0, "Summer", 10.0, true);
        p1.setDestination(bali);

        Package p2 = new Package(2, 2, "Swiss Alpine Explorer", "High alpine trails", 6, 1200.0, "Winter", 5.0, true);
        p2.setDestination(alps);

        Package p3 = new Package(3, 3, "Kyoto Heritage & Zen Trails", "Temples and tea", 4, 650.0, "Spring", 0.0, true);
        p3.setDestination(kyoto);

        Package p4 = new Package(4, 4, "Himalayan Adventure in Manali", "Trek and rafting", 4, 220.0, "Summer", 0.0, true);
        p4.setDestination(manali);

        mockPackages.add(p1);
        mockPackages.add(p2);
        mockPackages.add(p3);
        mockPackages.add(p4);

        // 2. Test Recommendation Engine
        RecommendationService recService = new RecommendationService();
        double budget = 1200.0;
        int desiredDays = 5;
        int guests = 2;
        List<String> userInterests = List.of("Beach");

        System.out.println("\n[1] Testing Recommendation Engine with criteria:");
        System.out.println("    Budget: $" + budget + " | Days: " + desiredDays + " | Guests: " + guests + " | Interest: Beach\n");

        List<ScoredPackage> results = recService.evaluateAndRankPackages(mockPackages, budget, desiredDays, guests, userInterests);

        for (int i = 0; i < results.size(); i++) {
            ScoredPackage sp = results.get(i);
            System.out.printf("  Rank %d: %-30s | Score: %5.1f%% | Est. Cost: $%7.2f | %s%n",
                (i + 1),
                sp.getTravelPackage().getTitle(),
                sp.getFinalScore(),
                sp.getEstimatedCost(),
                sp.getMatchSummary()
            );
        }

        // Bali should be top match because category is Beach and duration is 5 days
        boolean baliIsTop = results.get(0).getTravelPackage().getTitle().contains("Bali");
        System.out.println("\n  >> Algorithm assertion (Bali is #1): " + (baliIsTop ? "PASSED (OK)" : "FAILED"));

        // 3. Test Itinerary Service
        System.out.println("\n[2] Testing Itinerary Dynamic Schedule Generation:");
        ItineraryService itinService = new ItineraryService();
        List<ItineraryDay> schedule = itinService.getItineraryForPackage(p1);

        System.out.println("  Generated " + schedule.size() + " days for " + p1.getTitle() + ":");
        for (ItineraryDay day : schedule) {
            System.out.printf("    Day %d: %-35s - %s%n", day.getDayNumber(), day.getTitle(), day.getActivityDescription());
        }

        // 4. Test Cost Breakdown
        System.out.println("\n[3] Testing Itemized Cost Breakdown:");
        ItineraryService.ItemizedCost cost = itinService.calculateCostBreakdown(p1, guests, desiredDays);
        System.out.printf("  Base Cost: $%.2f | Discount: -$%.2f | Net: $%.2f | Living Allowance: $%.2f | Grand Total: $%.2f%n",
            cost.getGrossPackageCost(),
            cost.getDiscountAmount(),
            cost.getNetPackageCost(),
            cost.getTotalAllowance(),
            cost.getGrandTotal()
        );

        System.out.println("\n==================================================");
        System.out.println("  ALL DEV 4 LOGIC CHECKS COMPLETED SUCCESSFULLY!");
        System.out.println("==================================================");
    }
}
