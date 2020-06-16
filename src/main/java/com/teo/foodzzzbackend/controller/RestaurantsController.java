package com.teo.foodzzzbackend.controller;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.service.RestaurantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;


@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/foodz")
public class RestaurantsController {

    public static final int RESTAURANTS_PER_PAGE = 12;
    public static final int RESERVATIONS_PER_PAGE = 6;

    @Autowired
    RestaurantService restaurantService;

    @GetMapping("/restaurants/all")
    @CrossOrigin
    public ResponseEntity<Page<RestaurantDTO>> getAllRestaurants(@RequestParam String userId, @RequestParam Optional<String> pageNumber) {
        return new ResponseEntity<>(restaurantService.findAllRestaurantsPageable(userId, pageNumber.orElse("0"), RESTAURANTS_PER_PAGE), HttpStatus.OK);
    }

    @GetMapping("/restaurants/find")
    @CrossOrigin
    public Restaurant getRestaurant(@RequestParam String restaurantId) {
        return (restaurantService.findRestaurantById(restaurantId));
    }

    @GetMapping("/restaurants/keywords")
    @CrossOrigin
    public List<Tag> getTags(@RequestParam String restaurantId) {
        return (restaurantService.findAllTagsByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurants/reviews")
    @CrossOrigin
    public List<Review> getReviews(@RequestParam String restaurantId) {
        return (restaurantService.findAllReviewsByRestaurantId(Integer.parseInt(restaurantId)));
    }

//    @GetMapping("/restaurants/tables")
//    @CrossOrigin
//    public List<Table> getAllTablesByRestaurantId(@RequestParam String restaurantId) {
//        return (restaurantService.findAllTablesByRestaurantId(restaurantId));
//    }

    @GetMapping("/restaurants/table-forms")
    @CrossOrigin
    public List<TableForm> getAllTableFormsByRestaurantId(@RequestParam String restaurantId) {
        return (restaurantService.findTableFormsByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurants/search")
    @CrossOrigin
    public ModelMap showAdminPanel(@RequestParam String searchText, @RequestParam String pageNumber) {
        ModelMap model = new ModelMap();
        if (searchText == null && pageNumber == null) {
            return null;
        }

        if (searchText != null && pageNumber == null) {
            pageNumber = "1";
            model.put("pageNo", 1);
        }
        model.addAttribute("totalItems",
                restaurantService.searchRestaurantsTotalCount(searchText));
        model.addAttribute("pageCount",
                restaurantService.searchRestaurantsPagesCount(searchText, RESTAURANTS_PER_PAGE));
        model.addAttribute("restaurantsList",
                restaurantService.searchRestaurants(searchText, Integer.parseInt(pageNumber), RESTAURANTS_PER_PAGE));
        return model;
    }

    @GetMapping("/reservation/history")
    @CrossOrigin
    public List<ReservationDTO> getReservations(@RequestParam String userId) {
        return (restaurantService.findAllReservationsByUserId(userId));
    }

    @GetMapping("/reservation/history/pageable")
    @CrossOrigin
    public ResponseEntity<Page<ReservationDTO>> getReservationsPageable(@RequestParam String userId, @RequestParam Optional<String> pageNumber) {
        return new ResponseEntity<Page<ReservationDTO>>(restaurantService.findAllReservationsByUserIdPageable(userId, pageNumber.orElse("0"), RESERVATIONS_PER_PAGE), HttpStatus.OK);
    }

    @GetMapping("/reservation/history/restaurant")
    @CrossOrigin
    public ResponseEntity<List<ReservationDTO>> getRestaurantsReservationsPageable(
            @RequestParam String userId, @RequestParam String restaurantId,
            @RequestParam String reservationId, @RequestParam Optional<String> pageNumber) {
        return new ResponseEntity<List<ReservationDTO>>(restaurantService.findAllReservationsByUserIdAndRestaurantId(userId, restaurantId, reservationId), HttpStatus.OK);
    }

    @PostMapping(path = "/reservation/save", consumes = "application/json")
    @CrossOrigin
    public Reservation saveReservation(@RequestBody ReservationDTO reservation) {
        return (restaurantService.postReservation(reservation));
    }

    @GetMapping("/reservation/available")
    @CrossOrigin
    public List<ReservationDTO> getAllReservationsByRestaurantIdReservationDateAndReservationHour(
            @RequestParam String restaurantId, @RequestParam String reservationDate,
            @RequestParam String reservationHour) throws ParseException {
        return (restaurantService.findAllReservationsByRestaurantIdDateAndHour(restaurantId, reservationDate, reservationHour));
    }


    @PostMapping(path = "/restaurants/review/save", consumes = "application/json")
    @CrossOrigin
    public Review saveReview(@RequestBody ReviewDTO reviewDTO) {
        return (restaurantService.postReview(reviewDTO));
    }

}
