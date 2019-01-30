package com.upgrade.jurassicpark.controller;

import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Calendar;
import com.upgrade.jurassicpark.model.Reservation;
import com.upgrade.jurassicpark.service.ReservationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{email}")
    @ApiOperation(value = "View a list of active reservations for given email", response = Reservation.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
    }
    )
    public List<Reservation> findAllReservationsActive(
            @PathVariable
            @ApiParam(value = "Email to retrieve reservations", example = "john6832@gmail.com") String email){
        return reservationService.findAllReservationsActive(email);
    }

    @GetMapping("/{email}/cancelled")
    @ApiOperation(value = "View a list of cancelled reservations for given email", response = Reservation.class, responseContainer = "List")
    public List<Reservation> findAllReservationsCancelled(
            @PathVariable
            @ApiParam(value = "Email to retrieve cancelled reservations", example = "john6832@gmail.com") String email){
        return reservationService.findAllReservationsCancelled(email);
    }

    @GetMapping("/availability")
    @ApiOperation(value = "Returns a Calendar object with the day-per-day availability of reservations within specified range", response = Calendar.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Calendar showing availability for every day within specified range")
    })
    public Calendar getCalendar(

            @RequestParam(value = "from", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @ApiParam(
                    value = "Start date the user wish to calculate availability",
                    format = "yyyy-MM-dd'T'HH:mm:ss",
                    example = "2019-01-29T00:00:00") LocalDateTime from,

            @RequestParam(value = "to", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @ApiParam(
                    value = "End date the user wish to calculate availability",
                    format = "yyyy-MM-dd'T'HH:mm:ss",
                    example = "2019-01-29T00:00:00") LocalDateTime to

    ) {

        if (from == null) {
            from = LocalDate.now().atStartOfDay();
        }

        if (to == null) {
            to = LocalDate.now().atStartOfDay().plusMonths(1);
        }

        return reservationService.getReservationCalendar(from, to);
    }

    @PostMapping(path = "")
    @ApiOperation(value = "Create a new reservation", response = Reservation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reservation was created and the token retrieved"),
            @ApiResponse(code = 400, response = ExceptionResponse.class, message = "Reservation could not be created because of a validation error")
    })
    public ResponseEntity save(@Valid @RequestBody Reservation reservation) throws BookingTooSoonException,
            MaxDaysExceededException, BookingTooLateException, MaxReservationsPerDayExceededException, BookingInThePastException, EndDateBeforeStartDateException {

        Reservation savedReservation = reservationService.save(reservation);

        return ResponseEntity.ok(savedReservation.getToken());
    }

    @PutMapping(path = "/{token}")
    @ApiOperation(value = "Update reservation by token", response = Reservation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated reservation"),
            @ApiResponse(code = 404, message = "No reservation found with given token")
    }
    )
    public ResponseEntity<Reservation> update(@Valid @RequestBody Reservation reservation, @PathVariable String token) throws ReservationNotFoundException,
            BookingTooSoonException, MaxDaysExceededException, BookingTooLateException, MaxReservationsPerDayExceededException, BookingInThePastException, EndDateBeforeStartDateException {

        Reservation savedReservation = reservationService.findByToken(token);

        if (savedReservation == null) {
            throw new ReservationNotFoundException(token);
        }

        reservation.setId(savedReservation.getId());
        reservation.setToken(savedReservation.getToken());

        reservationService.save(reservation);

        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping(path = "/{token}")
    @ApiOperation(value = "Cancels reservation by token", response = Reservation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully cancelled reservation"),
            @ApiResponse(code = 404, message = "No reservation found with given token")
    }
    )
    public ResponseEntity cancel(@PathVariable String token) throws ReservationNotFoundException {
        Reservation reservation = reservationService.findByToken(token);

        if (reservation == null) {
            throw new ReservationNotFoundException(token);
        }

        reservationService.cancelReservation(reservation);

        return ResponseEntity.ok().build();
    }


}
