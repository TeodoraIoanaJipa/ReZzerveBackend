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
            logger.debug(" ReservationEventListener - event handling for "+ event.getReservationEmailInfoDto().getRestaurantName());
            ReservationStatus reservationStatus = event.getReservationStatus();
            if (reservationStatus != null) {
                emailService.sendReservationUpdatedByManagerEmail(event.getReservationEmailInfoDto(), reservationStatus);
                if (event.getReservationEmailInfoDto().getUsername() != null) {
                    logger.debug("ReservationEventListener - Reservation notification sent to user " +
                            event.getReservationEmailInfoDto().getUsername());
                }
            }
        } catch (Exception exception) {
            logger.error("ReservationEventListener - Could not send email to user : " + event.getReservationEmailInfoDto().getUsername());
        }
    }

}
