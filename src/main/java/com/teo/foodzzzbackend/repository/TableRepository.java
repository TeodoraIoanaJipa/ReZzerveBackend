package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
    List<Table> findAllByRestaurantId(int restaurantId);
}
