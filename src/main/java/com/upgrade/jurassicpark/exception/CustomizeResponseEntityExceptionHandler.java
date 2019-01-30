package com.upgrade.jurassicpark.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@RestController
@ControllerAdvice
public class CustomizeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookingInThePastException.class)
    protected ResponseEntity<ExceptionResponse> handleBookingInThePastException(BookingInThePastException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingTooLateException.class)
    protected ResponseEntity<ExceptionResponse> handleBookingTooLateException(BookingTooLateException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EndDateBeforeStartDateException.class)
    protected ResponseEntity<ExceptionResponse> handleEndDateBeforeStartDateException(EndDateBeforeStartDateException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingTooSoonException.class)
    protected ResponseEntity<ExceptionResponse> handleBookingTooSoonException(BookingTooSoonException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxDaysExceededException.class)
    protected ResponseEntity<ExceptionResponse> handleMaxDaysExceededException(MaxDaysExceededException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxReservationsPerDayExceededException.class)
    protected ResponseEntity<ExceptionResponse> handleMaxReservationsPerDayExceededException(MaxReservationsPerDayExceededException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    protected ResponseEntity<ExceptionResponse> handleReservationNotFoundException(ReservationNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ResponseEntity handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getField()+": '"+Objects.requireNonNull(ex.getBindingResult().getFieldError()).getRejectedValue()+ "': "+Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }
}
