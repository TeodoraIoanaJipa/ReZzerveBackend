package com.teo.foodzzzbackend.controller;


import com.teo.foodzzzbackend.model.Reservation;
import com.teo.foodzzzbackend.model.ReservationDTO;
import com.teo.foodzzzbackend.security.payload.response.MessageResponse;
import com.teo.foodzzzbackend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rezerve/reservation")
public class ReservationController {

    @Autowired
    RestaurantService restaurantService;

    @PostMapping(path = "/save", consumes = "application/json")
    @CrossOrigin
    public Reservation saveReservation(@RequestBody ReservationDTO reservation) {
        return (restaurantService.postReservation(reservation));
    }

    @GetMapping("/reservationConfirm/{reservationId}")
    @CrossOrigin
    public ResponseEntity<?> confirmReservation(@PathVariable Integer reservationId, @RequestParam Boolean isConfirmed) {
        try {
            Reservation reservation;
            if (isConfirmed) {
                reservation = restaurantService.confirmReservation(reservationId);
                if (reservation == null) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Rezervarea nu a putut fi confirmata."));
                }
            } else {
                reservation = restaurantService.declineReservation(reservationId);
                if (reservation == null) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Rezervarea nu a putut fi anulata."));
                }
            }
            String message = isConfirmed ? "Rezervarea dvs la restaurantul " + reservation.getRestaurant().getRestaurantName() + " a fost confirmata. Va multumim! " :
                    "Rezervarea dvs la restaurantul " + reservation.getRestaurant().getRestaurantName() + " a fost anulata. Vă mulțumim! ";

            return ResponseEntity.ok(new MessageResponse(message));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Token-ul este invalid."));
        }
    }
}
