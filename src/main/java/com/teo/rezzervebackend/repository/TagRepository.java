package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<List<Tag>> findAllByRestaurantId(int restaurantId);
}
