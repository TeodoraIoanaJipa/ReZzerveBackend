package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Restaurant;


import com.teo.foodzzzbackend.model.RestaurantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	List<Restaurant> findAll();

	Optional<Restaurant> findById(int restaurantId);

	@Query("SELECT new com.teo.foodzzzbackend.model.Restaurant(res.id, res.restaurantName, " +
			" res.pictureId, res.description, res.opensAt, res.closesAt, res.price) "
			+ "FROM Restaurant res LEFT JOIN res.manager manager where res.manager.id = ?1")
	Optional<Restaurant> findByManagerId(Long managerId);


	@Query("SELECT new com.teo.foodzzzbackend.model.RestaurantDTO(res.id, res.restaurantName, " +
			" res.pictureId, res.address.street) FROM Restaurant res ")
	Page<RestaurantDTO> findAllRestaurantsPageable(Pageable pageable);

}


