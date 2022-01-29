package com.teo.foodzzzbackend.model.exception;

public class ReservationStatusAlreadyChangedException extends Exception{

    public ReservationStatusAlreadyChangedException(String message) {
        super(message);
    }
}
