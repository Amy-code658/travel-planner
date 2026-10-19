package com.tourism.service;

import com.tourism.dao.ItineraryDAO;
import com.tourism.model.Destination;
import com.tourism.model.ItineraryDay;
import com.tourism.model.Package;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for day-by-day itinerary management and itemized cost estimation.
 * Fetches stored itineraries from the database or dynamically generates them
 * when pre-configured days are not available.
 */
public class ItineraryService {

    private final ItineraryDAO itineraryDAO;

    public ItineraryService() {
        this.itineraryDAO = new ItineraryDAO();
    }

    public ItineraryService(ItineraryDAO itineraryDAO) {
        this.itineraryDAO = itineraryDAO;
    }

    /**
     * Data structure holding itemized trip cost calculations.
     */
    public static class ItemizedCost {
        private final double basePricePerPerson;
        private final int guests;
        private final double discountPercentage;
        private final double grossPackageCost;
        private final double discountAmount;
        private final double netPackageCost;
        private final double dailyAllowancePerPerson;
        private final int days;
        private final double totalAllowance;
        private final double grandTotal;

        public ItemizedCost(double basePricePerPerson, int guests, double discountPercentage,
                            double dailyAllowancePerPerson, int days) {
            this.basePricePerPerson = basePricePerPerson;
            this.guests = Math.max(1, guests);
            this.discountPercentage = discountPercentage;
            this.dailyAllowancePerPerson = dailyAllowancePerPerson;
            this.days = Math.max(1, days);

            this.grossPackageCost = this.basePricePerPerson * this.guests;
            this.discountAmount = this.grossPackageCost * (this.discountPercentage / 100.0);
            this.netPackageCost = this.grossPackageCost - this.discountAmount;
            this.totalAllowance = this.dailyAllowancePerPerson * this.days * this.guests;
            this.grandTotal = this.netPackageCost + this.totalAllowance;
        }

        public double getBasePricePerPerson() {
            return basePricePerPerson;
        }

        public int getGuests() {
            return guests;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public double getGrossPackageCost() {
            return grossPackageCost;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public double getNetPackageCost() {
            return netPackageCost;
        }

        public double getDailyAllowancePerPerson() {
            return dailyAllowancePerPerson;
        }

        public int getDays() {
            return days;
        }

        public double getTotalAllowance() {
            return totalAllowance;
        }

        public double getGrandTotal() {
            return grandTotal;
        }
    }

    /**
     * Retrieves the day-wise itinerary for a given package.
     * If the database does not contain pre-defined days, an automated day-wise
     * schedule is generated based on destination category and trip length.
     *
     * @param pkg the travel package
     * @return List of ItineraryDay objects
     */
    public List<ItineraryDay> getItineraryForPackage(Package pkg) {
        if (pkg == null) {
            return new ArrayList<>();
        }

        List<ItineraryDay> stored = itineraryDAO.getItineraryByPackageId(pkg.getPackageId());
        if (stored != null && !stored.isEmpty()) {
            return stored;
        }

        // Automated generation fallback
        return generateDynamicItinerary(pkg);
    }

    /**
     * Generates an automated, contextual day-wise itinerary when no static records exist.
     */
    public List<ItineraryDay> generateDynamicItinerary(Package pkg) {
        List<ItineraryDay> days = new ArrayList<>();
        int totalDays = Math.max(1, pkg.getDurationDays());
        int pkgId = pkg.getPackageId();
        Destination dest = pkg.getDestination();
        String destName = (dest != null) ? dest.getName() : "Destination";
        String category = (dest != null) ? dest.getCategory() : "General";

        // Day 1: Arrival & Check-in
        days.add(new ItineraryDay(
            pkgId, 1,
            "Arrival & Check-in in " + destName,
            "Arrive at the destination, complete hotel check-in formalities, enjoy a welcome refreshment, and take an evening orientation stroll."
        ));

        // Intermediate days based on category
        for (int day = 2; day < totalDays; day++) {
            days.add(buildContextualDay(pkgId, day, totalDays, destName, category));
        }

        // Final Day: Souvenirs & Departure
        if (totalDays > 1) {
            days.add(new ItineraryDay(
                pkgId, totalDays,
                "Souvenirs & Departure from " + destName,
                "Enjoy breakfast at the resort, last-minute shopping at local artisan markets, check-out, and private transfer to the departure hub."
            ));
        }

        return days;
    }

    private ItineraryDay buildContextualDay(int pkgId, int dayNumber, int totalDays, String destName, String category) {
        String title;
        String desc;

        switch (category.toLowerCase()) {
            case "beach":
                if (dayNumber % 2 == 0) {
                    title = "Coastal Discovery & Water Activities";
                    desc = "Morning snorkeling and coastal kayaking along the bay. Afternoon relaxation by the seaside with tropical refreshments.";
                } else {
                    title = "Island Hopping & Sunset Cruise";
                    desc = "Full-day boat excursion visiting secluded lagoons, coral reefs, and an evening catamaran sunset dinner.";
                }
                break;
            case "adventure":
                if (dayNumber % 2 == 0) {
                    title = "Mountain Trails & Outdoor Quest";
                    desc = "Guided trek along scenic mountain ridges with panoramic vista viewpoints, followed by zip-lining and picnic lunch.";
                } else {
                    title = "River Expedition & Wilderness Excursion";
                    desc = "White-water rafting and nature exploration with experienced safety guides. Campfire gathering in the evening.";
                }
                break;
            case "culture":
                if (dayNumber % 2 == 0) {
                    title = "Heritage Monuments & Temple Walk";
                    desc = "Private guided tour of UNESCO historical sites, ancient architecture, and centuries-old local traditions.";
                } else {
                    title = "Culinary Masterclass & Artisan Markets";
                    desc = "Morning street-market visit for fresh ingredients, hands-on traditional cooking class, and artisan craft gallery visit.";
                }
                break;
            case "nature":
                if (dayNumber % 2 == 0) {
                    title = "National Park Safari & Flora Trail";
                    desc = "Early morning guided wildlife excursion, bird-watching walk, and botanical garden photography session.";
                } else {
                    title = "Valley Trekking & Waterfall Exploration";
                    desc = "Scenic forest trail leading to natural spring waterfalls, followed by quiet relaxation and wellness activities.";
                }
                break;
            case "luxury":
                if (dayNumber % 2 == 0) {
                    title = "Private Chauffeured Highlights & High Tea";
                    desc = "Exclusive private tour of premier landmarks, followed by an elegant afternoon tea and VIP boutique shopping.";
                } else {
                    title = "Wellness Spa Retreat & Fine Dining";
                    desc = "Full-day rejuvenating spa treatment and thermal baths, culminating in a 5-star Michelin-inspired tasting dinner.";
                }
                break;
            default:
                title = "City Highlights & Local Exploration";
                desc = "Explore the vibrant neighborhoods, renowned museums, and scenic public squares of " + destName + ".";
                break;
        }

        return new ItineraryDay(pkgId, dayNumber, title, desc);
    }

    /**
     * Calculates an itemized breakdown of expenses for a trip.
     */
    public ItemizedCost calculateCostBreakdown(Package pkg, int guests, int days) {
        if (pkg == null) {
            return new ItemizedCost(0, 1, 0, 0, 1);
        }

        Destination dest = pkg.getDestination();
        double dailyAllowance = (dest != null) ? dest.getEstimatedDailyCost() : 50.0;
        int tripDays = (days > 0) ? days : pkg.getDurationDays();

        return new ItemizedCost(
            pkg.getBasePrice(),
            guests,
            pkg.getDiscountPercentage(),
            dailyAllowance,
            tripDays
        );
    }
}
