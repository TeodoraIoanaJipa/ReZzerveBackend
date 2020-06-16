package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.Restaurant;


import com.teo.foodzzzbackend.model.RestaurantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    List<Restaurant> findAll();

    Optional<Restaurant> findById(int restaurantId);

    	@Query("SELECT new com.teo.foodzzzbackend.model.RestaurantDTO(res.id, res.restaurantName, res.address.street, " +
			" min(img.id) , avg(COALESCE(rev.rating, 0)) ) FROM Restaurant res left join Images img on res.id = img.restaurant.id" +
                " left JOIN Review rev on rev.restaurant.id = res.id" +
			" where res.id = ?1 group by res.id, res.restaurantName, res.address.street ")
	Optional<RestaurantDTO> findRestaurantId(Integer restaurantId);

    @Modifying
    @Query("update Restaurant res " +
            " set res.width = ?2, res.height = ?3 " +
            " where res.id = ?1 ")
    void updateRestaurantWidthAndHeight(Integer restaurantId, Integer width,Integer height);

    Optional<List<Restaurant>> findAllByManagerId(Long managerId);

    @Query("SELECT new com.teo.foodzzzbackend.model.RestaurantDTO(res.id, res.restaurantName, " +
            "  res.address.street, min(img.id), avg(rev.rating)) " +
            "FROM Restaurant res join Images img on res.id = img.restaurant.id " +
            "left join Review rev on rev.restaurant.id = res.id" +
            "  group by res.id, res.restaurantName, res.address.street")
    Page<RestaurantDTO> findAllRestaurantsPageable(Pageable pageable);


}


