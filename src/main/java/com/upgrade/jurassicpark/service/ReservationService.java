package com.upgrade.jurassicpark.service;

import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Calendar;
import com.upgrade.jurassicpark.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {

    Calendar getReservationCalendar(LocalDateTime from, LocalDateTime to);

    Reservation save(Reservation reservation) throws MaxDaysExceededException, MaxReservationsPerDayExceededException, BookingTooLateException, BookingTooSoonException, BookingInThePastException, EndDateBeforeStartDateException;

    Reservation findByToken(String token);

    List<Reservation> findAllReservationsActive(String email);

    List<Reservation> findAllReservationsCancelled(String email);

    void cancelReservation(Reservation reservation);
}
