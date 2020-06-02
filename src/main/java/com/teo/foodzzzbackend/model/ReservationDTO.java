package com.teo.foodzzzbackend.model;

import java.util.Date;

public class ReservationDTO {
    private Integer reservationId;
    private Date reservationDate;
    private int numberOfPersons;
    private String reservationHour;
    private String tableNumber;
    private int restaurantId;
    private String restaurantName;
    private Long userId;
    private boolean isReviewable;

    public ReservationDTO(Integer reservationId, Date reservationDate, int numberOfPersons, String reservationHour, String tableNumber, int restaurantId, String restaurantName, Long userId) {
        this.reservationId = reservationId;
        this.reservationDate = reservationDate;
        this.numberOfPersons = numberOfPersons;
        this.reservationHour = reservationHour;
        this.tableNumber = tableNumber;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userId = userId;
    }

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

    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public String getReservationHour() {
        return reservationHour;
    }

    public void setReservationHour(String reservationHour) {
        this.reservationHour = reservationHour;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isReviewable() {
        return isReviewable;
    }

    public void setReviewable(boolean reviewable) {
        isReviewable = reviewable;
    }
}
