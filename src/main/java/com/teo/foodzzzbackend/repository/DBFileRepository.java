package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DBFileRepository extends JpaRepository<Images, Long> {
    Optional<List<Images>> findAllByRestaurantId(int restaurantId);

    void deleteById(Long id);

    long countAllByRestaurant_Id(int restaurantId);
}