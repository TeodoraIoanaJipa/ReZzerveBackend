package com.teo.foodzzzbackend.model.dto;

import com.teo.foodzzzbackend.model.ReservationConfirmationStatus;

import java.util.Date;

public class ReservationEmailInfoDto {
    private Integer reservationId;
    private Date reservationDate;
    private String reservationHour;
    private String reservationStatus;
    private ReservationConfirmationStatus reservationConfirmationStatus;
    private String restaurantName;
    private String username;
    private String email;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationHour() {
        return reservationHour;
    }

    public void setReservationHour(String reservationHour) {
        this.reservationHour = reservationHour;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public ReservationConfirmationStatus getReservationConfirmationStatus() {
        return reservationConfirmationStatus;
    }

    public void setReservationConfirmationStatus(ReservationConfirmationStatus reservationConfirmationStatus) {
        this.reservationConfirmationStatus = reservationConfirmationStatus;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
