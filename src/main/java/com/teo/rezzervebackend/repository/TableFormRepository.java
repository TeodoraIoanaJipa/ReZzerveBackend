package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.TableForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableFormRepository  extends JpaRepository<TableForm, Integer> {

    Optional<List<TableForm>> findAllByRestaurantId(int restaurantId);

    void deleteByRestaurantId(int restaurantId);
}
