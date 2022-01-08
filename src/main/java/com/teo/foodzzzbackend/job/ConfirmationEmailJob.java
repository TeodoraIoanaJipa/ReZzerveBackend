package com.teo.foodzzzbackend.job;

import com.teo.foodzzzbackend.model.ReservationDTO;
import com.teo.foodzzzbackend.model.ReservationInfo;
import com.teo.foodzzzbackend.model.User;
import com.teo.foodzzzbackend.repository.ReservationRepository;
import com.teo.foodzzzbackend.repository.UserRepository;
import com.teo.foodzzzbackend.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ConfirmationEmailJob {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerService managerService;

    public ConfirmationEmailJob() {
    }

    private List<ReservationDTO> findAllReservations() {
        return reservationRepository.findAllReservationsForConfirmation();
    }

    private List<ReservationInfo> findAllReservationInfos() {
        List<ReservationInfo> reservations = new ArrayList<>();

        List<ReservationDTO> reservationDTOS = findAllReservations();
        for (ReservationDTO reservationDTO : reservationDTOS) {

            Optional<User> user = userRepository.findById(reservationDTO.getUserId());
            if (user.isPresent()) {
                ReservationInfo reservationInfo = new ReservationInfo();
                User user1 = user.get();
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
    public void cronJobSch() throws MessagingException {
        List<ReservationInfo> reservationInfos = findAllReservationInfos();

        for (ReservationInfo reservation : reservationInfos) {
            String msg = "<html>" +
                    "<body>" +
                    "<h4>Buna ziua,<b> " + reservation.getUsername() + " </b> ! <br></h4>" +
                    "<div>" +
                    "   Va multumim pentru rezervarea dumneavoastra la <b> " + reservation.getRestaurantName() + " </b>"
                    + ". <br> Va rugam sa confirmati rezervarea cu un click pe urmatorul link. Va multumim! " +
                    "</div>" +
                    "</body>" +
                    "</html>";

            managerService.sendSimpleMessage(reservation.getEmail(), "Rezervare la " + reservation.getRestaurantName(), msg);
        }
        System.out.println("Java cron job expression:: " + reservationInfos);
    }
}
