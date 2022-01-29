package com.teo.foodzzzbackend.controller;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.model.dto.RestaurantDto;
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
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/rezerve")
public class RestaurantsController {
    private final Logger logger = Logger.getLogger(RestaurantsController.class.getName());

    public static final int RESTAURANTS_PER_PAGE = 12;
    public static final int RESERVATIONS_PER_PAGE = 6;

    @Autowired
    RestaurantService restaurantService;

    @GetMapping("/restaurants/all")
    @CrossOrigin
    public ResponseEntity<Page<RestaurantDTO>> getAllRestaurants(@RequestParam String userId,
                                                                 @RequestParam String orderBy,
                                                                 @RequestParam Optional<String> pageNumber) {
        try {
            return new ResponseEntity<Page<RestaurantDTO>>(restaurantService.findAllRestaurantsPageable(userId, orderBy, pageNumber.orElse("0"), RESTAURANTS_PER_PAGE), HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/restaurants/all - Could not fetch restaurants. " + exception.getMessage());
            return new ResponseEntity("Could not fetch restaurants.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/restaurants/find")
    @CrossOrigin
    public ResponseEntity<Restaurant> getRestaurant(@RequestParam String restaurantId) {
        try {
            return new ResponseEntity<Restaurant>(restaurantService.findRestaurantById(restaurantId), HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/restaurants/find - Could not fetch restaurant " + restaurantId + "  " + exception.getMessage());
            return new ResponseEntity("Could not fetch restaurant.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/restaurants/details")
    @CrossOrigin
    public ResponseEntity<RestaurantDto> getRestaurantDetails(@RequestParam String restaurantId) {
        try {
            Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
            List<Tag> tags = restaurantService.findAllTagsByRestaurantId(restaurantId);
            List<Review> reviews = restaurantService.findAllReviewsByRestaurantId(Integer.parseInt(restaurantId));
            List<TableForm> tables = restaurantService.findTableFormsByRestaurantId(restaurantId);

            RestaurantDto restaurantDto = new RestaurantDto();
            restaurantDto.setId(restaurant.getId());
            restaurantDto.setRestaurantName(restaurant.getRestaurantName());
            restaurantDto.setDescription(restaurant.getDescription());
            restaurantDto.setOpensAt(restaurant.getOpensAt());
            restaurantDto.setClosesAt(restaurant.getClosesAt());
            restaurantDto.setPrice(restaurant.getPrice());
            restaurantDto.setKitchenTypes(restaurant.getKitchenTypes());
            restaurantDto.setLocalTypes(restaurant.getLocalTypes());
            restaurantDto.setAddress(restaurant.getAddress());
            restaurantDto.setImages(restaurant.getImages());
            restaurantDto.setWidth(restaurant.getWidth());
            restaurantDto.setHeight(restaurant.getHeight());
            restaurantDto.setRating(restaurant.getRating());

            restaurantDto.setTags(tags);
            restaurantDto.setReviews(reviews);
            restaurantDto.setTables(tables);

            return new ResponseEntity<RestaurantDto>(restaurantDto, HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/restaurants/details - Could not get details for " + restaurantId + "  " + exception.getMessage());
            return new ResponseEntity("Error ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/restaurants/reviews")
    @CrossOrigin
    public ResponseEntity<List<Review>> getReviews(@RequestParam String restaurantId) {
        try {
            return new ResponseEntity<List<Review>>(restaurantService.findAllReviewsByRestaurantId(Integer.parseInt(restaurantId)), HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/restaurants/keywords - Could not fetch keywords for " + restaurantId + "  " + exception.getMessage());
            return new ResponseEntity("Error ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/restaurants/table-forms")
    @CrossOrigin
    public List<TableForm> getAllTableFormsByRestaurantId(@RequestParam String restaurantId) {
        return (restaurantService.findTableFormsByRestaurantId(restaurantId));
    }

    @GetMapping("/reservation/history")
    @CrossOrigin
    public List<ReservationDTO> getReservations(@RequestParam String userId) {
        return (restaurantService.findAllReservationsByUserId(userId));
    }

    @GetMapping("/reservation/history/pageable")
    @CrossOrigin
    public ResponseEntity<Page<ReservationDTO>> getReservationsPageable(@RequestParam String userId, @RequestParam Optional<String> pageNumber) {
        return new ResponseEntity<Page<ReservationDTO>>(
                restaurantService.findAllReservationsByUserIdPageable(userId,
                        pageNumber.orElse("0"), RESERVATIONS_PER_PAGE), HttpStatus.OK);
    }

    @GetMapping("/reservation/history/restaurant")
    @CrossOrigin
    public ResponseEntity<List<ReservationDTO>> getRestaurantsReservationsPageable(
            @RequestParam String userId, @RequestParam String restaurantId,
            @RequestParam String reservationId, @RequestParam Optional<String> pageNumber) {
        return new ResponseEntity<List<ReservationDTO>>(restaurantService.findAllReservationsByUserIdAndRestaurantId(userId, restaurantId, reservationId), HttpStatus.OK);
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
