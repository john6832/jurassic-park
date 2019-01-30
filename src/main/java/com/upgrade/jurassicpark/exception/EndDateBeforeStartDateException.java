package com.upgrade.jurassicpark.exception;

public class EndDateBeforeStartDateException extends Exception {

    public EndDateBeforeStartDateException() {
        super("Sorry, end date should not be before start date");
    }
}
