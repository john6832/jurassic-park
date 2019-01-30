package com.upgrade.jurassicpark.exception;

public class MaxDaysExceededException extends Exception {

    public MaxDaysExceededException(Integer maxDaysAllowed) {
        super("Sorry, you cannot make a reservation for more than " + maxDaysAllowed + " days");
    }
}
