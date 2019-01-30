package com.upgrade.jurassicpark.exception;

public class BookingTooLateException extends Exception {

    public BookingTooLateException() {
        super("Sorry, you cannot book a reservation 1 day before your arrival");
    }
}
