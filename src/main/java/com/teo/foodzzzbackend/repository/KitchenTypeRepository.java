package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.KitchenType;
import com.teo.foodzzzbackend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitchenTypeRepository extends JpaRepository<KitchenType, Integer> {
    @Override
    List<KitchenType> findAll();

    Optional<KitchenType> findFirstByRestaurantsIn(List<Restaurant> restaurants);
}
