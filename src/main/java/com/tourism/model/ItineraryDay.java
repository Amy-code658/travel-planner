package com.tourism.model;

/**
 * ItineraryDay entity representing a single day in a package schedule.
 */
public class ItineraryDay {
    private int itineraryId;
    private int packageId;
    private int dayNumber;
    private String title;
    private String activityDescription;

    public ItineraryDay() {}

    public ItineraryDay(int itineraryId, int packageId, int dayNumber, String title, String activityDescription) {
        this.itineraryId = itineraryId;
        this.packageId = packageId;
        this.dayNumber = dayNumber;
        this.title = title;
        this.activityDescription = activityDescription;
    }

    public ItineraryDay(int packageId, int dayNumber, String title, String activityDescription) {
        this.packageId = packageId;
        this.dayNumber = dayNumber;
        this.title = title;
        this.activityDescription = activityDescription;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    @Override
    public String toString() {
        return "Day " + dayNumber + ": " + title;
    }
}
