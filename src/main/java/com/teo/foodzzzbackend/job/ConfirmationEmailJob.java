package com.teo.foodzzzbackend.job;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.repository.ReservationRepository;
import com.teo.foodzzzbackend.repository.UserRepository;
import com.teo.foodzzzbackend.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class ConfirmationEmailJob {

    private Logger logger = Logger.getLogger(ConfirmationEmailJob.class.getName()); ;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerService managerService;

    @Value("classpath:data/confirmation-message.html")
    private Resource resourceFile;

    @Value("${app.url}")
    private String appURL;

    private List<ReservationDTO> findAllReservationsForConfirmation() {
        return reservationRepository.findAllReservationsForConfirmation();
    }

    private List<ReservationInfo> findAllReservationThatNeedConfirmation() {
        List<ReservationInfo> reservations = new ArrayList<>();

        List<ReservationDTO> reservationDTOS = findAllReservationsForConfirmation();

        for (ReservationDTO reservationDTO : reservationDTOS) {
            Optional<User> user = userRepository.findById(reservationDTO.getUserId());
            if (user.isPresent()) {
                ReservationInfo reservationInfo = new ReservationInfo();
                User foundUser = user.get();
                reservationInfo.setId(reservationDTO.getReservationId());
                reservationInfo.setEmail(foundUser.getEmail());
                reservationInfo.setUsername(foundUser.getUsername());
                reservationInfo.setReservationDate(reservationDTO.getReservationDate());
                reservationInfo.setRestaurantName(reservationDTO.getRestaurantName());
                reservations.add(reservationInfo);
            }
        }

        return reservations;
    }

    @Scheduled(cron = "${cron.expression.confirmation.job}")
    public void cronJobSch() throws MessagingException, IOException {
        List<ReservationInfo> reservationInfos = findAllReservationThatNeedConfirmation();

        InputStream resource = resourceFile.getInputStream();
        String message;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            message = reader.lines()
                    .collect(Collectors.joining("\n"));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


        for (ReservationInfo reservation : reservationInfos) {
            String confirmReservationLink = this.appURL + "rezerve/reservation/reservationConfirm/" + reservation.getId().toString() + "?isConfirmed=true";
            String cancelReservationLink = this.appURL + "rezerve/reservation/reservationConfirm/" + reservation.getId().toString() + "?isConfirmed=false";
            String reservationDate = format.format(reservation.getReservationDate());
            String msg = message
                    .replace("{0}", reservation.getUsername())
                    .replace("{1}", reservation.getRestaurantName())
                    .replace("{2}", reservationDate)
                    .replace("{3}", confirmReservationLink)
                    .replace("{4}", cancelReservationLink);

            managerService.sendSimpleMessage(reservation.getEmail(), "Rezervare la " + reservation.getRestaurantName(), msg);
        }


        System.out.println("Job expression:: " + reservationInfos);
    }

    @Scheduled(cron = "${cron.expression.cancel.reservation.job}")
    public void cronJobCancelReservations() throws MessagingException, IOException {
        logger.log(Level.INFO, "Canceled reservations not confirmed");

        List<ReservationDTO> reservationDTOS = reservationRepository.findAllReservationsForCancelation();

        for (ReservationDTO reservationDTO : reservationDTOS) {
            Optional<Reservation> reservation = reservationRepository.findById(reservationDTO.getReservationId());
            if (reservation.isPresent()) {
                Reservation declinedReservation = reservation.get();
                declinedReservation.setReservationConfirmationStatus(ReservationConfirmationStatus.DECLINED);
                reservationRepository.save(declinedReservation);
            }
        }
    }
}
