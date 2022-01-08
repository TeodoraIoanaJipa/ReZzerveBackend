package com.teo.foodzzzbackend.job;

import com.teo.foodzzzbackend.model.ReservationDTO;
import com.teo.foodzzzbackend.model.ReservationInfo;
import com.teo.foodzzzbackend.model.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ConfirmationEmailJob {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerService managerService;

    @Value("classpath:data/confirmation-message.html")
    private Resource resourceFile;

    private List<ReservationDTO> findAllReservationsForConfirmation() {
        return reservationRepository.findAllReservationsForConfirmation();
    }

    private List<ReservationInfo> findAllReservationInfos() {
        List<ReservationInfo> reservations = new ArrayList<>();

        List<ReservationDTO> reservationDTOS = findAllReservationsForConfirmation();
        for (ReservationDTO reservationDTO : reservationDTOS) {

            Optional<User> user = userRepository.findById(reservationDTO.getUserId());
            if (user.isPresent()) {
                ReservationInfo reservationInfo = new ReservationInfo();
                User user1 = user.get();
                reservationInfo.setId(reservationDTO.getReservationId());
                reservationInfo.setEmail(user1.getEmail());
                reservationInfo.setUsername(user1.getUsername());
                reservationInfo.setReservationDate(reservationDTO.getReservationDate());
                reservationInfo.setRestaurantName(reservationDTO.getRestaurantName());
                reservations.add(reservationInfo);
            }
        }

        return reservations;
    }

    @Scheduled(cron = "${cron.expression.confirmation.job}")
    public void cronJobSch() throws MessagingException, IOException {
        List<ReservationInfo> reservationInfos = findAllReservationInfos();

        InputStream resource = resourceFile.getInputStream();
        String message;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            message = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        for (ReservationInfo reservation : reservationInfos) {
            String msg = message
                    .replace("{0}", reservation.getUsername())
                    .replace("{1}", reservation.getRestaurantName())
                    .replace("{2}", reservation.getId().toString());

            managerService.sendSimpleMessage(reservation.getEmail(), "Rezervare la " + reservation.getRestaurantName(), msg);
        }
        System.out.println("Java cron job expression:: " + reservationInfos);
    }
}
