package com.upgrade.jurassicpark.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

@ApiModel(description = "Object encapsulating the Jurassic Park availability during a given day")
public class Day implements Comparable<Day> {

    public Day(LocalDate date, Long reservationsMade, Integer maxReservations) {
        this.date = date;
        this.reservationsMade = reservationsMade;
        this.maxReservations = maxReservations;
        this.availableSpots = maxReservations - reservationsMade.intValue();
    }

    @ApiModelProperty(value = "Date for this current day", example = "2019-01-29")
    private LocalDate date;

    @ApiModelProperty(
            value = "Number of reservations already booked on Jurassic Park for this day",
            example = "21",
            notes = "This number should always be less than or equal to 'maxReservations'")
    private Long reservationsMade;

    @ApiModelProperty(
            value = "Maximum number of reservations allowed per day",
            example = "30",
            notes = "This value can be modified using the 'jurassic-world.max-reservations-per-day' property")
    private Integer maxReservations;

    @ApiModelProperty(
            value = "Number of spots available for reservation",
            example = "9")
    private Integer availableSpots;

    public LocalDate getDate() {
        return date;
    }

    public Long getReservationsMade() {
        return reservationsMade;
    }

    public Integer getMaxReservations() {
        return maxReservations;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    @Override
    public int compareTo(Day day) {
        return getDate().isBefore(day.getDate()) ? -1 : 1;
    }
}
