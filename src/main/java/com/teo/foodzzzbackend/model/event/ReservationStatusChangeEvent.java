package com.teo.foodzzzbackend.model.event;

import com.teo.foodzzzbackend.model.ReservationStatus;
import com.teo.foodzzzbackend.model.dto.ReservationEmailInfoDto;
import org.springframework.context.ApplicationEvent;

public class ReservationStatusChangeEvent extends ApplicationEvent {

    private final ReservationEmailInfoDto reservationEmailInfoDto;
    private final ReservationStatus reservationStatus;

    public ReservationStatusChangeEvent(Object source, ReservationEmailInfoDto reservationEmailInfoDto, ReservationStatus reservationStatus) {
        super(source);
        this.reservationEmailInfoDto = reservationEmailInfoDto;
        this.reservationStatus = reservationStatus;
    }

    public ReservationEmailInfoDto getReservationEmailInfoDto() {
        return reservationEmailInfoDto;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
}
