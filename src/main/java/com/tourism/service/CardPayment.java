package com.tourism.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dummy card payment. No real bank is contacted, we only check that the details look correct.
 * Concepts: class implementing an interface, method overriding, String methods, exception handling.
 */
public class CardPayment implements PaymentMethod {

    private final String holderName;
    private final String cardNumber;
    private final String expiry;      // format MM/YY
    private final String cvv;

    public CardPayment(String holderName, String cardNumber, String expiry, String cvv) {
        this.holderName = holderName == null ? "" : holderName.trim();
        this.cardNumber = cardNumber == null ? "" : cardNumber.replace(" ", "");
        this.expiry = expiry == null ? "" : expiry.trim();
        this.cvv = cvv == null ? "" : cvv.trim();
    }

    @Override
    public void validate() throws BookingException {
        if (holderName.isEmpty()) {
            throw new BookingException("Please enter the card holder name.");
        }
        if (!cardNumber.matches("\\d{16}")) {
            throw new BookingException("Card number must be exactly 16 digits.");
        }
        checkExpiry();
        if (!cvv.matches("\\d{3}")) {
            throw new BookingException("CVV must be exactly 3 digits.");
        }
    }

    // Expiry check: the card is valid until the end of the printed month
    private void checkExpiry() throws BookingException {
        YearMonth expiryMonth;
        try {
            expiryMonth = YearMonth.parse(expiry, DateTimeFormatter.ofPattern("MM/yy"));
        } catch (DateTimeParseException e) {
            throw new BookingException("Expiry must be in MM/YY format, for example 08/28.");
        }
        if (expiryMonth.isBefore(YearMonth.now())) {
            throw new BookingException("This card has expired.");
        }
    }

    @Override
    public String getDescription() {
        if (cardNumber.length() < 4) {
            return "Card";
        }
        return "Card ending " + cardNumber.substring(cardNumber.length() - 4);
    }
}
