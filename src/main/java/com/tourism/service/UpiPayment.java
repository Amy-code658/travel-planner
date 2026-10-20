package com.tourism.service;

/**
 * Dummy UPI payment. Only checks that the UPI id looks like name@bank.
 * Concepts: interface implementation, polymorphism (used through the PaymentMethod type).
 */
public class UpiPayment implements PaymentMethod {

    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId == null ? "" : upiId.trim();
    }

    @Override
    public void validate() throws BookingException {
        if (!upiId.matches("[\\w.\\-]+@[A-Za-z]+")) {
            throw new BookingException("Enter a valid UPI id, for example name@upi.");
        }
    }

    @Override
    public String getDescription() {
        return "UPI " + upiId;
    }
}
