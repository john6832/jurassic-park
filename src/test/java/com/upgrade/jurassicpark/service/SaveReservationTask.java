package com.upgrade.jurassicpark.service;

import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Reservation;

public class SaveReservationTask implements Runnable {

    private ReservationServiceImpl reservationService;
    private Reservation reservation;

    public SaveReservationTask(ReservationServiceImpl reservationService, Reservation reservation) {
        this.reservationService = reservationService;
        this.reservation = reservation;
    }

    @Override
    public void run() {
        try {
            reservationService.save(reservation);
        } catch (MaxDaysExceededException | MaxReservationsPerDayExceededException | EndDateBeforeStartDateException | BookingInThePastException | BookingTooSoonException | BookingTooLateException e) {
            e.printStackTrace();
        }
    }
}
