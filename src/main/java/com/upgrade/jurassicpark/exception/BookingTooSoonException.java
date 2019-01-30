package com.upgrade.jurassicpark.exception;

public class BookingTooSoonException extends Exception {

    public BookingTooSoonException() {
        super("Sorry, you cannot book a reservation more than 1 month before your arrival");
    }
}
