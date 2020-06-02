package com.teo.foodzzzbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
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
    ImageRepository imageRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    TableRepository tableRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

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

    private List<Review> findAllReviewsByRestaurantId(Integer restaurantId) {
        Optional<List<Review>> allReviews = reviewRepository.findAllByRestaurantId(restaurantId);
        List<Review> reviews = new ArrayList<>();
        if (allReviews.isPresent()) {
            reviews = allReviews.get();
            Collections.sort(reviews, (o1, o2) -> o1.getRating() - o2.getRating());
        }
        for (Review review : reviews) {
            review.setUsername(review.getUser().getUsername());
        }
        return reviews;
    }

    private Double getRestaurantRating(Integer restaurantId, List<Review> reviews) {
        Map<Integer, Integer> starsRatingMap = getStarsRatingMap(reviews);
        double weightedAverage = 0.0;
        Integer weightedSum = 0;
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
        restaurant.get().setRating(rating);
        restaurant.get().setReviews(reviews);
        return restaurant.orElse(null);
    }

    public List<Image> findAllImagesByRestaurantId(String restaurantId) {
        List<Image> images = new ArrayList<Image>();
        Optional<List<Image>> opt = imageRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        images = opt.orElse(null);
        return images;
    }

    public List<Tag> findAllTagsByRestaurantId(String restaurantId) {
        List<Tag> tags = new ArrayList<>();
        Optional<List<Tag>> opt = tagRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        tags = opt.orElse(null);
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
//        Page<ReservationDTO> pages = reservationRepository.findAllReservationsAndRestaurantsByUserId(Long.valueOf(userId), pageable);
//            checkReviewable(pages);
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
        Optional<Restaurant> restaurant = restaurantRepository.findById(reservation.getRestaurantId());
        restaurant.ifPresent(newReservation::setRestaurant);
        if (userRepository.findById(reservation.getUserId()).isPresent())
            newReservation.setUser(userRepository.findById(reservation.getUserId()).get());
        return reservationRepository.save(newReservation);
    }

    public Restaurant findRestaurantByManagerId(String managerId) {
        Optional<Restaurant> opt = restaurantRepository.findByManagerId(Long.valueOf(managerId));
        Restaurant managersRestaurant;
        managersRestaurant = opt.orElse(null);
        return managersRestaurant;
    }

    public List<Reservation> findAllReservationsByRestaurantId(String restaurantId) {
        List<Reservation> reservations;
        Optional<List<Reservation>> opt = reservationRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        reservations = opt.orElse(null);
        return reservations;
    }

    public List<Reservation> findAllPendingReservationsByRestaurantId(String restaurantId) {
        List<Reservation> reservations;
        Optional<List<Reservation>> opt = reservationRepository.findAllByRestaurantIdAndReservationStatus(Integer.parseInt(restaurantId), "pending");
        reservations = opt.orElse(null);
        return reservations;
    }

    public List<ReservationDTO> findAllReservationsByRestaurantIdReservationDateAndReservationHour(String restaurantId, String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(reservationDate);
        return reservationRepository.findAllReservationsByRestaurantIdAndReservationDate(Integer.parseInt(restaurantId),
                date, reservationHour);
    }

    public void updateReservationStatusToDeclined(String reservationId) {
        reservationRepository.changeReservationStatusToDeclined(Integer.parseInt(reservationId));
    }

    public void updateReservationStatusToAccepted(String reservationId) {
        reservationRepository.changeReservationStatusToAccepted(Integer.parseInt(reservationId));
    }

    public List<Table> findAllTablesByRestaurantId(String restaurantId) {
        List<Table> tables;
        tables = tableRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        return tables;
    }

    public Page<Restaurant> findAllRestaurantsPageable(String userId, String page, Integer pageSize) {
//        Pageable firstPageWithTwoElements = PageRequest.of(Integer.parseInt(page) - 1, pageSize, Sort.by("restaurantName"));

        JSONObject object = new JSONObject(getRestaurantsFromPyhtonAPI(Long.parseLong(userId)));
        JSONArray array = object.getJSONArray("restaurant_ids");
        System.out.println(array);

        ArrayList<Restaurant> recommendedRestaurants = new ArrayList<>();

        for (Object restaurantId : array.toList()) {
            Restaurant restaurant = findRestaurantById(restaurantId.toString());
            recommendedRestaurants.add(restaurant);
        }
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page) - 1, pageSize);
        int total = recommendedRestaurants.size();
        int start = (Integer.parseInt(page) - 1) * pageRequest.getPageSize();
        int end = Math.min((start + pageRequest.getPageSize()), total);
        System.out.println("end" + end);

        return new PageImpl<Restaurant>(
                recommendedRestaurants.subList(start, end),
                pageRequest,
                total
        );


//        Page<RestaurantDTO> restaurantDTOS = restaurantRepository.findAllRestaurantsPageable(firstPageWithTwoElements);
//
//        for (RestaurantDTO restaurant : restaurantDTOS) {
//            Integer id = restaurant.getId();
//            List<Review> reviews = findAllReviewsByRestaurantId(id);
//            Double rating = getRestaurantRating(id, reviews);
//            restaurant.setRating(rating);
//        }

//        return restaurantDTOS;
    }

    public FullTextQuery searchFullText(String searchText) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Restaurant.class)
                .get();

        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .keyword()
                .wildcard()        //it is necessary if we want to make use of wildcards
                .onFields("restaurantName", "description", "address.street", "tags.tagName")
                .boostedTo(5f)
                .matching(searchText + "*")
                .createQuery();

        //        org.apache.lucene.search.Sort sort = queryBuilder
//                .sort()
//                .byField("restaurantName")
//                .asc()
//                .createSort();
//        query.setSort(sort);
        return fullTextEntityManager.createFullTextQuery(luceneQuery, Restaurant.class);
    }

    @Transactional
    public List<Restaurant> searchRestaurants(String searchText, int pageNo, int resultsPerPage) {
        FullTextQuery jpaQuery = searchFullText(searchText);
        jpaQuery.setMaxResults(resultsPerPage);
        jpaQuery.setFirstResult((pageNo - 1) * resultsPerPage);

        return (List<Restaurant>) jpaQuery.getResultList();
    }

    public int searchRestaurantsPagesCount(String searchText, int resultsPerPage) {
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

}
