package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review save(Review review);

    Optional<List<Review>> findAllByRestaurantIdOrderByCreatedDateDesc(int restaurantId);
    Optional<List<Review>> findAllByRestaurantIdAndUserIdAndReservation_ReservationId(int restaurantId,
                                                                                      Long userId, Integer reservationId);
}
