package com.tourism.service;

/**
 * Interface for the simulated payment step. Card and UPI both promise to
 * validate themselves and describe themselves, but each does it in its own way.
 * Concepts: interface, polymorphism.
 */
public interface PaymentMethod {

    /** Checks the entered details. Throws BookingException with a friendly message if invalid. */
    void validate() throws BookingException;

    /** Short text for the confirmation message, e.g. "Card ending 4242". */
    String getDescription();
}
