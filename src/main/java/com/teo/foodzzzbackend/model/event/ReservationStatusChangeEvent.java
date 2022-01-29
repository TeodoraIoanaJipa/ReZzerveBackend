package com.teo.foodzzzbackend.model.event;

import com.teo.foodzzzbackend.model.Reservation;
import com.teo.foodzzzbackend.model.ReservationStatus;
import org.springframework.context.ApplicationEvent;

public class ReservationStatusChangeEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final ReservationStatus reservationStatus;

    public ReservationStatusChangeEvent(Object source, Reservation reservation, ReservationStatus reservationStatus) {
        super(source);
        this.reservation = reservation;
        this.reservationStatus = reservationStatus;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
}
