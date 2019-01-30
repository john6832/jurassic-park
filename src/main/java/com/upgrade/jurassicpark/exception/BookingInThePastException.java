package com.upgrade.jurassicpark.exception;

public class BookingInThePastException extends Exception {

    public BookingInThePastException() {
        super("Sorry, you cannot book a reservation in the past");
    }
}
