package com.teo.foodzzzbackend.service;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.model.dto.ReviewDTO;
import com.teo.foodzzzbackend.model.exception.ReservationStatusAlreadyChangedException;
import com.teo.foodzzzbackend.repository.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RestaurantService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KitchenTypeRepository kitchenTypeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    TableFormRepository tableFormRepository;

    private final RestTemplate restTemplate;

    public RestaurantService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getRestaurantsFromPyhtonAPI(Long userId) {
        String restaurantsURL = "http://localhost:5000/recommendation/api/v1.0/restaurants/{user_id}";
        try {
            ResponseEntity<String> response = this.restTemplate.getForEntity(restaurantsURL, String.class, userId);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public String getCollaborativeRecommendationFromAPI(Long userId) {
        String restaurantsURL = "http://localhost:5000/recommendation/api/restaurant-collaborative/{user_id}";
        try {
            ResponseEntity<String> response = this.restTemplate.getForEntity(restaurantsURL, String.class, userId);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public Page<RestaurantDTO> findAllRestaurantsPageable(String userId, String orderBy, String page, Integer pageSize) {
        int orderType = Integer.parseInt(orderBy);
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page) - 1, pageSize);

        if (orderType == 3 || orderType == 4) {
                return getRestaurantsByName(page, pageSize);
        } else {
            Pageable firstPage = PageRequest.of(Integer.parseInt(page) - 1, pageSize);
            Page<RestaurantDTO> restaurantDTOS;
            if (orderType == 0) {
                restaurantDTOS = restaurantRepository.findAllRestaurantsPageableOrderByPopularity(firstPage);
            } else if (orderType == 1) {
                restaurantDTOS = restaurantRepository.findAllRestaurantsPageableOrderByDate(firstPage);
            } else if (orderType == 2) {
                restaurantDTOS = restaurantRepository.findAllRestaurantsPageable(firstPage);
            } else {
                restaurantDTOS = restaurantRepository.findAllRestaurantsPageable(firstPage);
            }
            for (RestaurantDTO restaurantDTO : restaurantDTOS) {
                List<Restaurant> list = new ArrayList<>();
                list.add(restaurantRepository.findById(restaurantDTO.getId()).orElse(null));
                Optional<KitchenType> kt = kitchenTypeRepository.findFirstByRestaurantsIn(list);
                restaurantDTO.setKitchenType(kt.orElse(null));
            }
            return restaurantDTOS;
        }

    }

    private Page<RestaurantDTO> getRestaurantsByName(String page, Integer pageSize) {
        Pageable firstPage = PageRequest.of(Integer.parseInt(page) - 1, pageSize);
        Page<RestaurantDTO> restaurantDTOS = restaurantRepository.findAllRestaurantsPageableOrderByPopularity(firstPage);
        for (RestaurantDTO restaurantDTO : restaurantDTOS) {
            List<Restaurant> list = new ArrayList<>();
            list.add(restaurantRepository.findById(restaurantDTO.getId()).orElse(null));
            Optional<KitchenType> kt = kitchenTypeRepository.findFirstByRestaurantsIn(list);
            restaurantDTO.setKitchenType(kt.orElse(null));
        }
        return restaurantDTOS;
    }

    @Transactional
    public List<RestaurantDTO> searchRestaurants(String searchText, int pageNo, int resultsPerPage) {
        FullTextQuery jpaQuery = searchFullText(searchText);
        jpaQuery.setMaxResults(resultsPerPage);
        jpaQuery.setFirstResult((pageNo - 1) * resultsPerPage);

        List<Restaurant> allRestaurants = (List<Restaurant>) jpaQuery.getResultList();
        List<RestaurantDTO> restaurantDTOS = new ArrayList<>();

        for (Restaurant restaurant : allRestaurants) {
            RestaurantDTO restaurantDTO = findRestaurantDTOById(restaurant.getId().toString());
            List<Restaurant> list = new ArrayList<>();
            list.add(restaurant);
            Optional<KitchenType> kt = kitchenTypeRepository.findFirstByRestaurantsIn(list);
            restaurantDTO.setKitchenType(kt.orElse(null));
            restaurantDTOS.add(restaurantDTO);
        }
        return restaurantDTOS;
    }

    private Map<Integer, Integer> getStarsRatingMap(List<Review> reviews) {
        Map<Integer, Integer> numberOfStars = new HashMap<>();
        for (Review review : reviews) {
            if (review.getRating() >= 1 && review.getRating() <= 5) {
                if (numberOfStars.containsKey(review.getRating())) {
                    Integer oldNumber = numberOfStars.get(review.getRating());
                    numberOfStars.put(review.getRating(), oldNumber + 1);
                } else {
                    numberOfStars.put(review.getRating(), 1);
                }
            }
        }
        return numberOfStars;
    }

    public List<Review> findAllReviewsByRestaurantId(Integer restaurantId) {
        Optional<List<Review>> allReviews = reviewRepository.findAllByRestaurantIdOrderByCreatedDateDesc(restaurantId);
        List<Review> reviews = new ArrayList<>();
        if (allReviews.isPresent()) {
            reviews = allReviews.get();
            reviews.sort(Comparator.comparingInt(Review::getRating));
        }
        for (Review review : reviews) {
            review.setUsername(review.getUser().getUsername());
        }
        return reviews;
    }

    private Double getRestaurantRating(Integer restaurantId, List<Review> reviews) {
        Map<Integer, Integer> starsRatingMap = getStarsRatingMap(reviews);
        double weightedAverage = 0.0;
        int weightedSum = 0;
        for (Map.Entry<Integer, Integer> pair : starsRatingMap.entrySet()) {
            weightedAverage += pair.getKey() * pair.getValue();
            weightedSum += pair.getValue();
        }
        if (weightedSum != 0) {
            return weightedAverage / weightedSum;
        }
        return (double) 0;
    }

    public Restaurant findRestaurantById(String restaurantId) {
        Integer id = Integer.parseInt(restaurantId);
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);

        List<Review> reviews = findAllReviewsByRestaurantId(id);

        Double rating = getRestaurantRating(id, reviews);
        if(restaurant.isPresent()){
            restaurant.get().setRating(Double.parseDouble(String.format("%.2f", rating)));
            restaurant.get().setReviews(reviews);
        }
        return restaurant.orElse(null);
    }

    public RestaurantDTO findRestaurantDTOById(String restaurantId) {
        Integer id = Integer.parseInt(restaurantId);
        Optional<RestaurantDTO> restaurant = restaurantRepository.findRestaurantId(id);
        restaurant.get().setRating(Double.parseDouble(String.format("%.2f", restaurant.get().getRating())));
        return restaurant.orElse(null);
    }

    public List<TableForm> findTableFormsByRestaurantId(String restaurantId) {
        Integer id = Integer.parseInt(restaurantId);
        Optional<List<TableForm>> tables = tableFormRepository.findAllByRestaurantId(id);
        return tables.orElse(null);
    }

    public List<Tag> findAllTagsByRestaurantId(String restaurantId) {
        Optional<List<Tag>> opt = tagRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        List<Tag> tags = opt.orElse(null);
        return tags;
    }

    public List<ReservationDTO> findAllReservationsByUserId(String userId) {
        List<ReservationDTO> allReservationDTOS = reservationRepository.findAllReservationsAndRestaurantsByUserId(Long.valueOf(userId));
        for (ReservationDTO reservation : allReservationDTOS) {
            if (reservation.getReservationDate().before(new Date()))
                if (!reviewRepository.findAllByRestaurantIdAndUserIdAndReservation_ReservationId(reservation.getRestaurantId(),
                        reservation.getUserId(), reservation.getReservationId()).isPresent()) {
                    reservation.setReviewable(true);
                } else {
                    reservation.setReviewable(false);
                }
        }
        return allReservationDTOS;
    }

    public Page<ReservationDTO> checkReviewable(Page<ReservationDTO> reservations) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        for (ReservationDTO reservation : reservations) {

            String reservationH = reservation.getReservationHour().split(":")[0];
            if (reservation.getReservationDate().before(yesterday)
                || (reservation.getReservationDate().equals(new Date()) && (Integer.parseInt(reservationH) < new Date().getHours())))
                    if (!reviewRepository.findAllByRestaurantIdAndUserIdAndReservation_ReservationId(
                            reservation.getRestaurantId(),
                            reservation.getUserId(),
                            reservation.getReservationId()).isPresent()) {
                        reservation.setReviewable(true);
                    } else {
                        reservation.setReviewable(false);
                    }
        }
        return reservations;
    }

    public List<ReservationDTO> findAllReservationsByUserIdAndRestaurantId(String userId, String restaurantId, String reservationId) {
        List<ReservationDTO> reservations =
                reservationRepository.findAllReservationsAndRestaurantsByUserIdAndRestaurantId(Long.valueOf(userId),
                        Integer.valueOf(restaurantId), Integer.valueOf(reservationId));
        for (ReservationDTO reservation : reservations) {
            System.out.println(reservation.toString());
            if (reservation.getReservationDate().before(new Date()))
                if (!reviewRepository.findAllByRestaurantIdAndUserIdAndReservation_ReservationId(reservation.getRestaurantId(),
                        reservation.getUserId(), reservation.getReservationId()).isPresent()) {
                    reservation.setReviewable(true);
                } else {
                    reservation.setReviewable(false);
                }
        }
        return reservations;
    }

    public Page<ReservationDTO> findAllReservationsByUserIdPageable(String userId, String page, Integer pageSize) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, pageSize);

        Page<ReservationDTO> reservations = reservationRepository.findAllByUserIdPageable(Long.valueOf(userId), pageable);
        return checkReviewable(reservations);
    }

    public Reservation postReservation(ReservationDTO reservation) {
        Reservation newReservation = new Reservation();
        newReservation.setNumberOfPersons(reservation.getNumberOfPersons());
        newReservation.setReservationHour(reservation.getReservationHour());
        newReservation.setReservationDate(reservation.getReservationDate());
        newReservation.setTableNumber(reservation.getTableNumber());
        newReservation.setReservationStatus("pending");
        newReservation.setRequestedDate(new Date());
        newReservation.setReservationConfirmationStatus(ReservationConfirmationStatus.IN_PROGRESS);
        Optional<Restaurant> restaurant = restaurantRepository.findById(reservation.getRestaurantId());
        restaurant.ifPresent(newReservation::setRestaurant);
        if (userRepository.findById(reservation.getUserId()).isPresent())
            newReservation.setUser(userRepository.findById(reservation.getUserId()).get());
        return reservationRepository.save(newReservation);
    }

    public Restaurant findRestaurantByManagerId(String managerId) {
        Optional<List<Restaurant>> opt = restaurantRepository.findAllByManagerId(Long.valueOf(managerId));
        List<Restaurant> managersRestaurant = opt.orElse(null);
        return managersRestaurant != null ? managersRestaurant.get(0) : null;
    }

    public List<Reservation> findAllReservationsByRestaurantId(String restaurantId) {
        List<Reservation> reservations;
        Optional<List<Reservation>> opt = reservationRepository.findAllByRestaurantIdOrderByReservationDateDesc(Integer.parseInt(restaurantId));
        reservations = opt.orElse(null);
        return reservations;
    }

    public List<Reservation> findAllPendingReservationsByRestaurantId(String restaurantId) {
        List<Reservation> reservations;
        Optional<List<Reservation>> opt = reservationRepository.findAllByRestaurantIdAndReservationStatus(Integer.parseInt(restaurantId), "pending");
        reservations = opt.orElse(null);
        return reservations;
    }

    public List<ReservationDTO> findAllReservationsByRestaurantIdDateAndHour(String restaurantId, String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(reservationDate);
        return reservationRepository.findAllReservationsByRestaurantIdAndReservationDate(Integer.parseInt(restaurantId),
                date, reservationHour);
    }


    public FullTextQuery searchFullText(String searchText) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Restaurant.class)
                .get();

        org.apache.lucene.search.Sort sort = queryBuilder
                .sort()
                .byField("restaurantName")
                .asc()
                .createSort();

        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .keyword()
                .wildcard()
                .onFields("restaurantName", "price", "description", "address.street",
                        "tags.tagName", "localTypes.localType", "kitchenTypes.kitchenName")
                .boostedTo(5f)
                .matching(searchText + "*")
                .createQuery();

        return fullTextEntityManager.createFullTextQuery(luceneQuery, Restaurant.class).setSort(sort);
    }


    public int searchRestaurantsPagesCount(String searchText, long resultsPerPage) {
        long userCount = searchRestaurantsTotalCount(searchText);
        return (int) Math.floorDiv(userCount, resultsPerPage) + 1;
    }

    @Transactional
    public int searchRestaurantsTotalCount(String searchText) {
        FullTextQuery jpaQuery = searchFullText(searchText);
        return jpaQuery.getResultSize();
    }

    public Review postReview(ReviewDTO reviewDTO) {
        Review newReview = new Review();
        newReview.setComment(reviewDTO.getComment());
        newReview.setRating(reviewDTO.getRating());
        newReview.setCreatedDate(new Date());

        Optional<Reservation> reservation = reservationRepository.findById(reviewDTO.getReservationId());
        reservation.ifPresent(newReview::setReservation);

        Optional<Restaurant> restaurant = restaurantRepository.findById(reviewDTO.getRestaurantId());
        restaurant.ifPresent(newReview::setRestaurant);

        Optional<User> user = userRepository.findById(reviewDTO.getUserId());
        user.ifPresent(newReview::setUser);

        return reviewRepository.save(newReview);
    }

    public Reservation confirmReservation(Integer reservationId) throws ReservationStatusAlreadyChangedException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (reservation.isPresent()) {
            Reservation foundReservation = reservation.get();
            if (foundReservation.getReservationConfirmationStatus().equals(ReservationConfirmationStatus.DECLINED)) {
                throw new ReservationStatusAlreadyChangedException("Rezervarea dumneavostra la restaurantul " +
                        foundReservation.getRestaurant().getRestaurantName() + " a fost deja anulata.");
            }

            if (foundReservation.getReservationConfirmationStatus().equals(ReservationConfirmationStatus.CONFIRMED)) {
                throw new ReservationStatusAlreadyChangedException("Rezervarea dumneavostra la restaurantul " +
                        foundReservation.getRestaurant().getRestaurantName() + " a fost deja acceptata.");
            }
            foundReservation.setReservationConfirmationStatus(ReservationConfirmationStatus.CONFIRMED);
            reservationRepository.save(foundReservation);
            return foundReservation;
        }
        return null;
    }


    public Reservation declineReservation(Integer reservationId) throws ReservationStatusAlreadyChangedException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (reservation.isPresent()) {
            Reservation foundReservation = reservation.get();
            if (foundReservation.getReservationConfirmationStatus().equals(ReservationConfirmationStatus.DECLINED)) {
                throw new ReservationStatusAlreadyChangedException("Rezervarea dumneavostra la restaurantul " +
                        foundReservation.getRestaurant().getRestaurantName() + " a fost deja anulata.");
            }

            if (foundReservation.getReservationConfirmationStatus().equals(ReservationConfirmationStatus.CONFIRMED)) {
                throw new ReservationStatusAlreadyChangedException("Rezervarea dumneavostra la restaurantul " +
                        foundReservation.getRestaurant().getRestaurantName() + " a fost deja acceptata.");
            }
            foundReservation.setReservationConfirmationStatus(ReservationConfirmationStatus.DECLINED);
            reservationRepository.save(foundReservation);
            return foundReservation;
        }
        return null;
    }

    public boolean getBenefitsForUserAndRestaurant(String restaurantId, String userId){
        Long currentUserId = Long.parseLong(userId);
        if (userRepository.findById(currentUserId).isPresent()){
            Integer currentRestaurantId = Integer.parseInt(restaurantId);
            if(restaurantRepository.findRestaurantId(currentRestaurantId).isPresent()){
                Optional<List<Review>> topRatedReviews = reviewRepository.findAllByRestaurantIdAndUserIdAndRating(currentRestaurantId, currentUserId, 5);
                return topRatedReviews.isPresent();
            }
        }
        return false;
    }

}
