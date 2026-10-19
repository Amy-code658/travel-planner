package com.tourism.service;

import com.tourism.dao.PackageDAO;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core Rule-Based Recommendation Service.
 * Scores and ranks packages based on budget, duration, and category match.
 *
 * Scoring Formula:
 * FinalScore = (w1 * BudgetMatch + w2 * DurationMatch + w3 * InterestMatch) * 100
 */
public class RecommendationService {

    // Configurable algorithmic weights (sum = 1.0)
    private static final double WEIGHT_BUDGET = 0.40;
    private static final double WEIGHT_DURATION = 0.35;
    private static final double WEIGHT_INTEREST = 0.25;

    private final PackageDAO packageDAO;

    public RecommendationService() {
        this.packageDAO = new PackageDAO();
    }

    public RecommendationService(PackageDAO packageDAO) {
        this.packageDAO = packageDAO;
    }

    /**
     * Data carrier representing a scored package recommendation.
     */
    public static class ScoredPackage {
        private final Package travelPackage;
        private final double finalScore;      // 0 - 100%
        private final double budgetScore;     // 0 - 100%
        private final double durationScore;   // 0 - 100%
        private final double interestScore;   // 0 - 100%
        private final double estimatedCost;
        private final String matchSummary;

        public ScoredPackage(Package travelPackage, double finalScore, double budgetScore,
                             double durationScore, double interestScore, double estimatedCost,
                             String matchSummary) {
            this.travelPackage = travelPackage;
            this.finalScore = Math.round(finalScore * 10.0) / 10.0;
            this.budgetScore = Math.round(budgetScore * 10.0) / 10.0;
            this.durationScore = Math.round(durationScore * 10.0) / 10.0;
            this.interestScore = Math.round(interestScore * 10.0) / 10.0;
            this.estimatedCost = Math.round(estimatedCost * 100.0) / 100.0;
            this.matchSummary = matchSummary;
        }

        public Package getTravelPackage() {
            return travelPackage;
        }

        public double getFinalScore() {
            return finalScore;
        }

        public double getBudgetScore() {
            return budgetScore;
        }

        public double getDurationScore() {
            return durationScore;
        }

        public double getInterestScore() {
            return interestScore;
        }

        public double getEstimatedCost() {
            return estimatedCost;
        }

        public String getMatchSummary() {
            return matchSummary;
        }

        public int getScorePercentage() {
            return (int) Math.round(finalScore);
        }
    }

    /**
     * Evaluates and ranks all active packages from the database according to user preferences.
     *
     * @param maxBudget           maximum total budget for the trip
     * @param desiredDays         target duration in days
     * @param guests              number of travelers
     * @param preferredCategories list of categories (e.g. Adventure, Beach, Culture)
     * @return List of ScoredPackage sorted in descending order of final score
     */
    public List<ScoredPackage> getRecommendations(double maxBudget, int desiredDays, int guests, List<String> preferredCategories) {
        List<Package> packages = packageDAO.findAllActive();
        return evaluateAndRankPackages(packages, maxBudget, desiredDays, guests, preferredCategories);
    }

    /**
     * Core scoring evaluation engine on a given list of packages.
     * Accessible for testing or custom package sets.
     */
    public List<ScoredPackage> evaluateAndRankPackages(List<Package> packages, double maxBudget,
                                                      int desiredDays, int guests,
                                                      List<String> preferredCategories) {
        List<ScoredPackage> scoredList = new ArrayList<>();
        if (packages == null || packages.isEmpty()) {
            return scoredList;
        }

        int travelerCount = Math.max(1, guests);
        int targetDays = Math.max(1, desiredDays);
        double budgetLimit = Math.max(50.0, maxBudget);

        for (Package pkg : packages) {
            Destination dest = pkg.getDestination();
            double dailyAllowance = (dest != null) ? dest.getEstimatedDailyCost() : 50.0;

            // Total estimated cost = discounted package price + living allowance for all guests
            double packageCost = pkg.getFinalPrice() * travelerCount;
            double livingCost = dailyAllowance * pkg.getDurationDays() * travelerCount;
            double estimatedTotal = packageCost + livingCost;

            // 1. Budget Match Score (0.0 to 1.0)
            double budgetMatch;
            if (estimatedTotal <= budgetLimit) {
                // Within budget: reward closeness to budget while remaining affordable
                double savingsRatio = (budgetLimit - estimatedTotal) / budgetLimit;
                budgetMatch = 1.0 - (savingsRatio * 0.25);
            } else {
                // Exceeds budget: penalize proportionally, 0 if > 30% over
                double overspend = estimatedTotal - budgetLimit;
                double maxTolerance = budgetLimit * 0.30;
                budgetMatch = Math.max(0.0, 1.0 - (overspend / maxTolerance));
            }

            // 2. Duration Match Score (0.0 to 1.0)
            int diffDays = Math.abs(pkg.getDurationDays() - targetDays);
            double durationMatch = Math.max(0.0, 1.0 - ((double) diffDays / targetDays));

            // 3. Interest / Category Match Score (0.0 to 1.0)
            double interestMatch;
            String category = (dest != null) ? dest.getCategory() : "";
            if (preferredCategories == null || preferredCategories.isEmpty()) {
                // User has no specific preference; neutral match
                interestMatch = 0.70;
            } else {
                boolean matched = false;
                for (String pref : preferredCategories) {
                    if (pref != null && pref.trim().equalsIgnoreCase(category.trim())) {
                        matched = true;
                        break;
                    }
                }
                interestMatch = matched ? 1.0 : 0.15;
            }

            // Weighted final score calculation
            double rawScore = (WEIGHT_BUDGET * budgetMatch) +
                              (WEIGHT_DURATION * durationMatch) +
                              (WEIGHT_INTEREST * interestMatch);

            double finalScore = Math.min(100.0, Math.max(0.0, rawScore * 100.0));

            // Generate human-readable match summary
            String summary = buildMatchReason(budgetMatch, durationMatch, interestMatch, category);

            scoredList.add(new ScoredPackage(
                pkg, finalScore, budgetMatch * 100.0, durationMatch * 100.0,
                interestMatch * 100.0, estimatedTotal, summary
            ));
        }

        // Sort descending by final score
        scoredList.sort(Comparator.comparingDouble(ScoredPackage::getFinalScore).reversed());
        return scoredList;
    }

    private String buildMatchReason(double budgetMatch, double durationMatch, double interestMatch, String category) {
        StringBuilder sb = new StringBuilder();
        if (interestMatch >= 0.9) {
            sb.append("Matches your interest in ").append(category).append(". ");
        }
        if (durationMatch >= 0.9) {
            sb.append("Perfect duration fit. ");
        } else if (durationMatch >= 0.7) {
            sb.append("Close to requested duration. ");
        }
        if (budgetMatch >= 0.8) {
            sb.append("Well within budget.");
        } else if (budgetMatch >= 0.5) {
            sb.append("Slightly above budget.");
        }
        return sb.toString().trim();
    }
}
