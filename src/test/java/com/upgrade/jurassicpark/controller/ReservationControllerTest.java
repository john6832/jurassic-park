package com.upgrade.jurassicpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.jurassicpark.exception.*;
import com.upgrade.jurassicpark.model.Calendar;
import com.upgrade.jurassicpark.model.Day;
import com.upgrade.jurassicpark.model.Reservation;
import com.upgrade.jurassicpark.model.ReservationStatus;
import com.upgrade.jurassicpark.service.ReservationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ReservationController.class, secure = false)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @MockBean
    private ReservationService reservationService;

    private Reservation reservation;
    private Calendar calendar;

    @Before
    public void setUp() {
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setName("Johnn Guerrero");
        reservation.setEmail("john6832@gmail.com");
        reservation.setToken(UUID.randomUUID().toString());
        reservation.setArrivalDate(LocalDate.now().atStartOfDay());
        reservation.setReservationStatus(ReservationStatus.ACTIVE);
        reservation.setDepartureDate(LocalDate.now().plusDays(2).atStartOfDay());


        List<Day> days = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Day day = new Day(LocalDate.now().plusDays(i), 0L, 30);
            days.add(day);
        }

        calendar = new Calendar(days);
    }


    @Test
    public void testFindAllReservationsActive() throws Exception {

        Mockito.when(
                reservationService.findAllReservationsActive(Mockito.anyString())).thenReturn(Collections.singletonList(reservation));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/api/reservations/" + reservation.getEmail()).accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = jsonObjectMapper.writeValueAsString(Collections.singleton(reservation));

        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void testFindAllReservationsCancelled() throws Exception {

        Mockito.when(
                reservationService.findAllReservationsCancelled(Mockito.anyString())).thenReturn(Collections.singletonList(reservation));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/api/reservations/" + reservation.getEmail() + "/cancelled").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = jsonObjectMapper.writeValueAsString(Collections.singleton(reservation));

        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void testGetCalendar() throws Exception {

        Mockito.when(
                reservationService.getReservationCalendar(Mockito.any(), Mockito.any())).thenReturn(calendar);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/api/reservations/availability").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = jsonObjectMapper.writeValueAsString(calendar);

        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void testSave() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenReturn(reservation);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }


    @Test
    public void testSaveReservationWithArrivalDateOnThePast() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new BookingInThePastException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, you cannot book a reservation in the past"));
    }

    @Test
    public void testSaveReservationOneDayBeforeArrivalDate() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new BookingTooLateException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, you cannot book a reservation 1 day before your arrival"));
    }

    @Test
    public void testSaveReservationWithMoreThanOneMonthBeforeArrivalDate() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new BookingTooSoonException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, you cannot book a reservation more than 1 month before your arrival"));
    }

    @Test
    public void testSaveReservationWithEndDateBeforeStartDate() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new EndDateBeforeStartDateException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, end date should not be before start date"));
    }

    @Test
    public void testSaveReservationExceedingMaxDaysInReservation() throws Exception {

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new MaxDaysExceededException(111));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, you cannot make a reservation for more than 111 days"));
    }

    @Test
    public void testSaveReservationExceedingMaxReservationsPerDay() throws Exception {

        List<LocalDate> invalidDates = Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1));

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenThrow(new MaxReservationsPerDayExceededException(invalidDates));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/reservations")
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Sorry, Jurassic Park is full on following day(s): " + invalidDates.stream().map(LocalDate::toString).collect(Collectors.joining(", ")) + ", so we cannot book your reservation"));
    }



    @Test
    public void testUpdate() throws Exception {

        Mockito.when(
                reservationService.findByToken(Mockito.anyString())).thenReturn(reservation);

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenReturn(reservation);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/reservations/"+reservation.getToken())
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testUpdateWithInvalidToken() throws Exception {

        Mockito.when(
                reservationService.findByToken(Mockito.anyString())).thenReturn(null);

        Mockito.when(
                reservationService.save(Mockito.any(Reservation.class))).thenReturn(reservation);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/reservations/"+reservation.getToken())
                .content(jsonObjectMapper.writeValueAsString(reservation))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Reservation not found with token: "+reservation.getToken()));
    }

    @Test
    public void testCancel() throws Exception {

        Mockito.when(
                reservationService.findByToken(Mockito.anyString())).thenReturn(reservation);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/reservations/"+reservation.getToken());

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testCancelWithInvalidToken() throws Exception {

        Mockito.when(
                reservationService.findByToken(Mockito.anyString())).thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/reservations/"+reservation.getToken());

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        assertTrue(result.getResponse().getContentAsString().contains("Reservation not found with token: "+reservation.getToken()));

    }


}
