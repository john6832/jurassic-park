package com.upgrade.jurassicpark.exception;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MaxReservationsPerDayExceededException extends Exception {

    public MaxReservationsPerDayExceededException(List<LocalDate> invalidDates) {
        super("Sorry, Jurassic Park is full on following day(s): " + invalidDates.stream().map(LocalDate::toString).collect(Collectors.joining(", ")) + ", so we cannot book your reservation");
    }
}
