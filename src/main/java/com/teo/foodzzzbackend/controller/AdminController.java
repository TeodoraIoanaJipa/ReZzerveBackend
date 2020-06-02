package com.teo.foodzzzbackend.controller;

import com.teo.foodzzzbackend.model.Reservation;
import com.teo.foodzzzbackend.model.Restaurant;
import com.teo.foodzzzbackend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    RestaurantService restaurantService;

    @GetMapping("/restaurant")
    @CrossOrigin
    public Restaurant getRestaurantData(@RequestParam String managerId) {
        return (restaurantService.findRestaurantByManagerId(managerId));
    }

    @GetMapping("/reservations/pending")
    @CrossOrigin
    public List<Reservation> getPendingReservations(@RequestParam String restaurantId) {
        return (restaurantService.findAllReservationsByRestaurantId(restaurantId));
    }

    @PutMapping("/reservations/decline")
    @CrossOrigin
    public ResponseEntity<String> updateReservationStatusToDeclined(@RequestParam String reservationId){
        restaurantService.updateReservationStatusToDeclined(reservationId);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PutMapping("/reservations/accept")
    @CrossOrigin
    public ResponseEntity<String> updateReservationStatusToAccepted(@RequestParam String reservationId){
        restaurantService.updateReservationStatusToAccepted(reservationId);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
