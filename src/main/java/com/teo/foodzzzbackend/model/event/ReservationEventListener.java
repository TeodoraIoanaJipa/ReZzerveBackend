package com.teo.foodzzzbackend.model.event;

import com.teo.foodzzzbackend.model.ReservationStatus;
import com.teo.foodzzzbackend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ReservationEventListener.class);

    @Autowired
    private EmailService emailService;

    @EventListener
    @Async
    public void handle(ReservationStatusChangeEvent event) {
        try {
            ReservationStatus reservationStatus = event.getReservationStatus();
            if (reservationStatus != null) {
                emailService.sendReservationUpdatedByManagerEmail(event.getReservation(), reservationStatus);
            }
        } catch (Exception exception) {
            logger.error("ReservationEventListener - Could not send email to user : " + event.getReservation().getUser());
        }
    }

}
