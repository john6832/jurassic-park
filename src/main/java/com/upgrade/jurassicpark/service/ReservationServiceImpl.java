package com.upgrade.jurassicpark.service;

import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Calendar;
import com.upgrade.jurassicpark.model.Day;
import com.upgrade.jurassicpark.model.Reservation;
import com.upgrade.jurassicpark.model.ReservationStatus;
import com.upgrade.jurassicpark.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Value("${jurassic-world.max-reservations-per-day}")
    private Integer maxReservationsPerDay;

    @Value("${jurassic-world.maximum-days-in-reservation}")
    private Integer maximumDaysInReservation;

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }


    @Override
    public Calendar getReservationCalendar(LocalDateTime from, LocalDateTime to) {

        List<Day> days = new ArrayList<>();

        while (from.isBefore(to) || from.isEqual(to)) {
            days.add(new Day(
                    from.toLocalDate(),
                    reservationRepository.countAllByArrivalDateIsLessThanEqualAndDepartureDateIsGreaterThanEqual(
                            from.toLocalDate().atStartOfDay(),
                            from.toLocalDate().atStartOfDay()),
                    maxReservationsPerDay));

            from = from.plusDays(1);
        }

        Collections.sort(days);

        return new Calendar(days);
    }

    public Reservation save(Reservation reservation) throws MaxDaysExceededException,
            MaxReservationsPerDayExceededException, BookingTooLateException, BookingTooSoonException,
            BookingInThePastException, EndDateBeforeStartDateException {

        System.out.println("Started: "+LocalDateTime.now());

        LocalDate startDate = reservation.getArrivalDate().toLocalDate();
        LocalDate endDate = reservation.getDepartureDate().toLocalDate();

        if(endDate.isBefore(startDate)){
            throw new EndDateBeforeStartDateException();
        }

        if(startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())){
            throw new BookingInThePastException();
        }

        if (startDate.minusDays(1).isBefore(LocalDate.now()) || startDate.minusDays(1).isEqual(LocalDate.now())) {
            throw new BookingTooLateException();
        }

        if (startDate.minusMonths(1).isAfter(LocalDate.now()) || startDate.minusMonths(1).isEqual(LocalDate.now())) {
            throw new BookingTooSoonException();
        }

        if (endDate.minusDays(maximumDaysInReservation).isAfter(startDate) || endDate.minusDays(maximumDaysInReservation).isEqual(startDate)) {
            throw new MaxDaysExceededException(maximumDaysInReservation);
        }

        //Synchronizing only this block allows for validations above still take place on different threads

        synchronized (this) {
            List<LocalDate> invalidDates = new ArrayList<>();

            while (!startDate.isAfter(endDate)) {

                if (reservationRepository.countAllByArrivalDateIsLessThanEqualAndDepartureDateIsGreaterThanEqual(
                        startDate.atStartOfDay(),
                        startDate.atStartOfDay()) >= maxReservationsPerDay) {

                    invalidDates.add(startDate);
                }

                startDate = startDate.plusDays(1);
            }

            if (!invalidDates.isEmpty()) {
                throw new MaxReservationsPerDayExceededException(invalidDates);
            }



            if (reservation.getToken() == null) {
                reservation.setToken(UUID.randomUUID().toString());
            }

            reservation.setReservationStatus(ReservationStatus.ACTIVE);


            System.out.println("Finished: "+LocalDateTime.now());

            return reservationRepository.save(reservation);
        }

    }

    @Override
    public Reservation findByToken(String token) {
        return reservationRepository.findByToken(token);
    }

    @Override
    public void cancelReservation(Reservation reservation) {
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> findAllReservationsActive(String email) {
        return reservationRepository.findAllByEmailAndReservationStatus(email, ReservationStatus.ACTIVE);
    }

    @Override
    public List<Reservation> findAllReservationsCancelled(String email) {
        return reservationRepository.findAllByEmailAndReservationStatus(email, ReservationStatus.CANCELLED);
    }
}
