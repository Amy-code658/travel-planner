package com.tourism.model;

/**
 * Transportation entity representing travel logistics bundled within a travel package.
 * Directly reflects the Class Diagram and domain model specifications.
 */
public class Transportation {

    private int transportId;
    private String type; // Flight, Train, Private Van, Cruise, etc.
    private double cost;

    public Transportation() {}

    public Transportation(int transportId, String type, double cost) {
        this.transportId = transportId;
        this.type = type;
        this.cost = cost;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return type + " ($" + cost + ")";
    }
}
