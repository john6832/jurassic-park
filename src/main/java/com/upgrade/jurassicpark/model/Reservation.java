package com.upgrade.jurassicpark.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@ApiModel(description = "Object encapsulating the the booking details on the Jurassic Park")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 9045098179799205444L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "Unique alpha-numeric text to identify reservation", example = "2d7428a6-b58c-4008-8575-f05549f16316")
    @Length(min = 36, max = 36)
    private String token;

    @ApiModelProperty(value = "Booking user name", example = "Johnn Guerrero")
    @Length(min = 2, max = 100)
    @NotNull
    private String name;

    @ApiModelProperty(value = "Booking user email", example = "john6832@gmail.com")
    @Length(min = 2, max = 100)
    @Email
    @NotNull
    private String email;

    @ApiModelProperty(value = "Reservation starting date", example = "2019-02-02T00:00:00")
    @NotNull
    private LocalDateTime arrivalDate;

    @ApiModelProperty(value = "Reservation ending date", example = "2019-02-04T00:00:00")
    @NotNull
    private LocalDateTime departureDate;

    @ManyToOne
    @JsonIgnore
    private ReservationStatus reservationStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getToken(), that.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getToken());
    }
}
