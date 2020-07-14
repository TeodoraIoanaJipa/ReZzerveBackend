package com.teo.foodzzzbackend.security;

import com.teo.foodzzzbackend.model.User;
import com.teo.foodzzzbackend.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private UserDetailsServiceImpl service;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent onRegistrationCompleteEvent) {
        this.sendConfirmRegistration(onRegistrationCompleteEvent);
    }

    private void sendSimpleMessage(User user, String subject, String confirmationUrl) {
        SimpleMailMessage email = new SimpleMailMessage();

        String recipientAddress = user.getEmail();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Bună, " + user.getUsername() + " ! \r\n" +
                "Te rugam confirmă adresa de email printr-un click aici \n" + confirmationUrl + "\n Multumim! ");
        mailSender.send(email);
    }

    private void sendReservationConfirmation(User user, String subject, String confirmationUrl) {
        SimpleMailMessage email = new SimpleMailMessage();

        String recipientAddress = user.getEmail();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Bună, " + user.getUsername() + " ! \r\n" +
                "Te rugam confirmă adresa de email printr-un click aici \n" + confirmationUrl + "\n Multumim! ");
        mailSender.send(email);
    }

    private void sendConfirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        service.createVerificationToken(user, token);

//        String confirmationUrl = "http://localhost:4200/registration-confirm/"+ token;
        String confirmationUrl = "https://foodzzz-a4f2c.web.app/registration-confirm/"+ token;
        String subject = "Registration Confirmation";
        sendSimpleMessage(user, subject, confirmationUrl);
    }
}
