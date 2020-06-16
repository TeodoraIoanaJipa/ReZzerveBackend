package com.teo.foodzzzbackend.controller;

import com.teo.foodzzzbackend.model.ERole;
import com.teo.foodzzzbackend.model.Role;
import com.teo.foodzzzbackend.model.User;
import com.teo.foodzzzbackend.model.VerificationToken;
import com.teo.foodzzzbackend.repository.RoleRepository;
import com.teo.foodzzzbackend.repository.UserRepository;
import com.teo.foodzzzbackend.security.OnRegistrationCompleteEvent;
import com.teo.foodzzzbackend.security.jwt.JwtUtils;
import com.teo.foodzzzbackend.security.payload.request.LoginRequest;
import com.teo.foodzzzbackend.security.payload.request.SignUpRequest;
import com.teo.foodzzzbackend.security.payload.response.JwtResponse;
import com.teo.foodzzzbackend.security.payload.response.MessageResponse;
import com.teo.foodzzzbackend.security.service.UserDetailsImpl;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.teo.foodzzzbackend.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = {"http://localhost:4200","https://foodzzz-a4f2c.web.app"})
@RestController
@RequestMapping("/api/foodz/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl service;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Email sau parolă invalide."));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        if (userDetails.isEmailEnabled())
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        else
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Email-ul nu a fost confirmat încă."));

    }

    @GetMapping("/registrationConfirm")
    @CrossOrigin
    public ResponseEntity<?> confirmRegistration(@RequestParam String token) {
        try {
            VerificationToken verificationToken = service.getVerificationToken(token);

            if (verificationToken == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Token de înregistrare invalid."));
            }

            User user = verificationToken.getUser();
            Calendar cal = Calendar.getInstance();
            if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Tokenul de înregistrare expiră dupa 24h."));
            }

            user.setEnabled(true);
            service.saveRegisteredUser(user);
            service.deleteVerificationToken(token);
            return ResponseEntity.ok(new MessageResponse("Confirmarea email-ului a avut loc cu succes. Vă mulțumim! Acum puteți accesa contul."));
        }catch(Exception exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Token-ul este invalid."));
        }

    }

    @GetMapping("/resendRegistrationToken")
    @CrossOrigin
    public ResponseEntity<?> resendRegistrationToken(@RequestParam String existingToken, HttpServletRequest request) {
//        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        User user = service.getUser(existingToken);
        service.deleteVerificationToken(existingToken);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale()));

        return ResponseEntity.ok(new MessageResponse("Email-ul a fost trimis catre dumneavoastra. "));
    }

    @GetMapping("/resendRegistrationTokenByEmail")
    @CrossOrigin
    public ResponseEntity<?> resendRegistrationTokenByEmail(@RequestParam String email, HttpServletRequest request) {
//        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        User user = service.findUserByUsername(email);
//        service.deleteVerificationToken(existingToken);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent((User) user, request.getLocale()));

        return ResponseEntity.ok(new MessageResponse("Email-ul a fost trimis către dumneavoastră. "));
    }

    @CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                          HttpServletRequest request) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Acest username este luat. Te rugam introdu un alt username!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Exista deja un cont cu acest email!"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getPhoneNumber());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Nu exista acest rol."));
                        roles.add(adminRole);
                        break;

                    case "manager":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User userSaved = userRepository.save(user);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userSaved, request.getLocale()));

        return ResponseEntity.ok(new MessageResponse("Utilizator înregistrat cu succes!"));
    }

}
