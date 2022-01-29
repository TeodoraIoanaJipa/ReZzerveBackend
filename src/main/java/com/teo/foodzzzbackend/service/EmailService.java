package com.teo.foodzzzbackend.service;

import com.teo.foodzzzbackend.model.Reservation;
import com.teo.foodzzzbackend.model.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("classpath:data/manager-accepted-message.html")
    private Resource acceptedReservationResourceFile;

    @Value("classpath:data/manager-declined-message.html")
    private Resource declinedReservationResourceFile;

    public String getEmailTemplate(InputStream resource) throws MessagingException, IOException {

        String message = "";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            message = reader.lines()
                    .collect(Collectors.joining("\n"));
        }
        return message;
    }

    public void sendReservationUpdatedByManagerEmail(Reservation reservation, ReservationStatus status) throws ParseException, MessagingException, IOException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String reservationDate = formatter.format(reservation.getReservationDate());

        InputStream resource = acceptedReservationResourceFile.getInputStream();

        InputStream declinedResourceFile = declinedReservationResourceFile.getInputStream();
        String declinedMessage = getEmailTemplate(declinedResourceFile);

        String emailText = (status.equals(ReservationStatus.DECLINED)) ? declinedMessage : getEmailTemplate(resource);
        emailText = emailText.replace("{0}", reservation.getUser().getUsername())
                .replace("{1}", reservation.getRestaurant().getRestaurantName())
                .replace("{2}", reservationDate)
                .replace("{3}", reservation.getReservationHour());

        String recipientAddress = reservation.getUser().getEmail();

        sendReservationConfirmationEmail(recipientAddress,
                "Rezervare la restaurantul " + reservation.getRestaurant().getRestaurantName(),
                emailText);
    }


    public void sendReservationConfirmationEmail(
            String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(mimeMessage);
    }
}
