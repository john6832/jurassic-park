package com.upgrade.jurassicpark.service;

import com.upgrade.jurassicpark.JurassicparkApplication;
import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Calendar;
import com.upgrade.jurassicpark.model.Day;
import com.upgrade.jurassicpark.model.Reservation;
import com.upgrade.jurassicpark.model.ReservationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JurassicparkApplication.class, ReservationServiceImpl.class})
public class ReservationServiceTest {

    @Autowired
    private ReservationServiceImpl reservationService;

    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;

    @Before
    public void setUp() {
        reservation1 = new Reservation();
        reservation1.setName("Jonas Guerrero");
        reservation1.setEmail("john6832@gmail.com");
        reservation1.setArrivalDate(LocalDate.now().plusDays(4).atStartOfDay());
        reservation1.setReservationStatus(ReservationStatus.ACTIVE);
        reservation1.setDepartureDate(LocalDate.now().plusDays(6).atStartOfDay());

        reservation2 = new Reservation();
        reservation2.setName("Mark Guerrero");
        reservation2.setEmail("john6832@gmail.com");
        reservation2.setArrivalDate(LocalDate.now().plusDays(4).atStartOfDay());
        reservation2.setReservationStatus(ReservationStatus.ACTIVE);
        reservation2.setDepartureDate(LocalDate.now().plusDays(6).atStartOfDay());

        reservation3 = new Reservation();
        reservation3.setName("Steven Guerrero");
        reservation3.setEmail("john6832@gmail.com");
        reservation3.setArrivalDate(LocalDate.now().plusDays(4).atStartOfDay());
        reservation3.setReservationStatus(ReservationStatus.ACTIVE);
        reservation3.setDepartureDate(LocalDate.now().plusDays(6).atStartOfDay());

    }

    @Test
    public void testFindByToken() throws Exception {
        Reservation reservation1 = reservationService.save(this.reservation1);

        assertEquals(reservation1.getEmail(), reservationService.findByToken(reservation1.getToken()).getEmail());

    }

    @Test
    public void testCancelReservation() throws Exception {
        Reservation reservation1 = reservationService.save(this.reservation1);

        reservationService.cancelReservation(reservation1);

        assertEquals(ReservationStatus.CANCELLED, reservationService.findByToken(reservation1.getToken()).getReservationStatus());

    }

    @Test
    public void testFindAll() throws Exception {

        Reservation reservation1 = reservationService.save(this.reservation1);
        Reservation reservation2 = reservationService.save(this.reservation2);
        Reservation reservation3 = reservationService.save(this.reservation3);

        List<Reservation> reservations = reservationService.findAllReservationsActive("john6832@gmail.com");

        assertEquals(3, reservations.size());
        assertEquals(reservation1.getToken(), reservations.get(0).getToken());
        assertEquals(reservation2.getToken(), reservations.get(1).getToken());
        assertEquals(reservation3.getToken(), reservations.get(2).getToken());

    }

    @Test
    public void testFindAllCancelled() throws Exception {

        Reservation reservation1 = reservationService.save(this.reservation1);
        Reservation reservation2 = reservationService.save(this.reservation2);
        Reservation reservation3 = reservationService.save(this.reservation3);

        reservationService.cancelReservation(reservation1);
        reservationService.cancelReservation(reservation2);
        reservationService.cancelReservation(reservation3);

        List<Reservation> reservations = reservationService.findAllReservationsCancelled("john6832@gmail.com");

        assertEquals(3, reservations.size());
        assertEquals(reservation1.getToken(), reservations.get(0).getToken());
        assertEquals(reservation2.getToken(), reservations.get(1).getToken());
        assertEquals(reservation3.getToken(), reservations.get(2).getToken());

    }

    @Test
    public void testFindAllWithInvalidEmail() throws Exception {

        reservationService.save(this.reservation1);
        reservationService.save(this.reservation2);
        reservationService.save(this.reservation3);

        List<Reservation> reservations = reservationService.findAllReservationsActive("john6832@heroku.com");

        assertEquals(0, reservations.size());

    }

    @Test
    public void testSave() throws Exception {

        Reservation reservation = reservationService.save(this.reservation1);

        List<Reservation> reservations = reservationService.findAllReservationsActive("john6832@gmail.com");

        assertEquals(1, reservations.size());
        assertEquals(reservation.getToken(), reservations.get(0).getToken());

    }

    @Test(expected = BookingInThePastException.class)
    public void testSaveWithArrivalDateOnThePast() throws Exception {

        reservation1.setArrivalDate(LocalDateTime.now().minusDays(1));
        reservationService.save(this.reservation1);

    }

    @Test(expected = BookingTooLateException.class)
    public void testSaveBookingTooLate() throws Exception {

        reservation1.setArrivalDate(LocalDateTime.now().plusHours(23));
        reservationService.save(this.reservation1);

    }

    @Test(expected = BookingTooSoonException.class)
    public void testSaveBookingTooSoon() throws Exception {

        reservation1.setArrivalDate(LocalDateTime.now().plusMonths(2));
        reservation1.setDepartureDate(LocalDateTime.now().plusMonths(2).plusDays(2));
        reservationService.save(this.reservation1);

    }

    @Test(expected = EndDateBeforeStartDateException.class)
    public void testSaveWithEndDateBeforeStartDate() throws Exception {

        reservation1.setDepartureDate(LocalDateTime.now().plusDays(1));
        reservationService.save(this.reservation1);

    }

    @Test(expected = MaxDaysExceededException.class)
    public void testSaveWithMaxDaysExceeded() throws Exception {

        reservation1.setDepartureDate(LocalDateTime.now().plusDays(9));
        reservationService.save(this.reservation1);

    }



    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetReservationCalendar() throws Exception {

        reservationService.save(this.reservation1);
        reservationService.save(this.reservation2);
        reservationService.save(this.reservation3);

        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusMonths(1).atStartOfDay();

        Calendar calendar = reservationService.getReservationCalendar(startDate, endDate);


        assertEquals(ChronoUnit.DAYS.between(startDate, endDate) + 1, calendar.getDays().size());
        assertEquals(3L, (long) calendar.getDays().get(4).getReservationsMade());
        assertEquals(27, (long) calendar.getDays().get(4).getAvailableSpots());

    }

    @Test(expected = MaxReservationsPerDayExceededException.class)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testSaveWithMaxReservationsPerDayExceeded() throws Exception {

        for (int i = 0; i < 30; i++) {
            reservation3 = new Reservation();
            reservation3.setName("Steven Guerrero");
            reservation3.setEmail("john6832@gmail.com");
            reservation3.setArrivalDate(LocalDate.now().plusDays(4).atStartOfDay());
            reservation3.setDepartureDate(LocalDate.now().plusDays(6).atStartOfDay());
            reservationService.save(reservation3);
        }

        reservationService.save(this.reservation1);


    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testConcurrentSaveWithMaxReservationsPerDayExceeded() {

        try {

            // Booking 35 reservations for the same dates using 8 different threads
            // 30 of them should be accepted, 5 of them should be rejected

            ExecutorService executorService = Executors.newFixedThreadPool(8);

            for (int i = 0; i < 35; i++) {
                reservation3 = new Reservation();
                reservation3.setName("Johnn Guerrero");
                reservation3.setEmail("john6832@gmail.com");
                reservation3.setArrivalDate(LocalDate.now().plusDays(4).atStartOfDay());
                reservation3.setDepartureDate(LocalDate.now().plusDays(6).atStartOfDay());

                executorService.submit(new SaveReservationTask(reservationService, reservation3));
            }

            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            // explicitly ignore rejected reservations
        }


        List<Reservation> reservations = reservationService.findAllReservationsActive("john6832@gmail.com");

        assertEquals(30, reservations.size());


    }



}
