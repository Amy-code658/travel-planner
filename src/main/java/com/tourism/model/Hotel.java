package com.tourism.model;

/**
 * Hotel entity representing accommodation options bundled within a travel package.
 * Directly reflects the Class Diagram and domain model specifications.
 */
public class Hotel {

    private int hotelId;
    private String name;
    private double pricePerNight;

    public Hotel() {}

    public Hotel(int hotelId, String name, double pricePerNight) {
        this.hotelId = hotelId;
        this.name = name;
        this.pricePerNight = pricePerNight;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    @Override
    public String toString() {
        return name + " ($" + pricePerNight + "/night)";
    }
}
