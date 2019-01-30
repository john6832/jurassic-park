package com.upgrade.jurassicpark.exception;

public class ReservationNotFoundException extends Exception {

    public ReservationNotFoundException(String token) {
        super("Reservation not found with token: "+token);
    }
}
