package com.tourism.service;

/**
 * Our own (custom) exception for anything that goes wrong while booking, paying or cancelling.
 * Concepts: inheritance (extends Exception), custom exceptions, exception chaining (cause).
 */
public class BookingException extends Exception {

    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
