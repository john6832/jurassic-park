package com.upgrade.jurassicpark.repository;

import com.upgrade.jurassicpark.model.Reservation;
import com.upgrade.jurassicpark.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Reservation findByToken(String token);

    List<Reservation> findAllByEmailAndReservationStatus(String email, ReservationStatus reservationStatus);

    Long countAllByArrivalDateIsLessThanEqualAndDepartureDateIsGreaterThanEqual(LocalDateTime from, LocalDateTime to);


}
