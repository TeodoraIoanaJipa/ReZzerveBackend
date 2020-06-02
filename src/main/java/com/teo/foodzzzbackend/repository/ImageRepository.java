package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<List<Image>> findAllByRestaurantId(int restaurantId);
    List<Image> findAll();
}
