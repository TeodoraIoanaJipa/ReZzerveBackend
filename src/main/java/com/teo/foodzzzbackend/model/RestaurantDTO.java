package com.teo.foodzzzbackend.model;

import java.util.HashSet;
import java.util.Set;

public class RestaurantDTO {
    private Integer id;
    private String restaurantName;
    private String pictureId;
    private String street;
    private Double rating;
    private Set<KitchenType> kitchenTypes = new HashSet<>();

    public RestaurantDTO(Integer id, String restaurantName, String pictureId, String street, Set<KitchenType> kitchenTypes, Double rating) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.pictureId = pictureId;
        this.street = street;
        this.kitchenTypes = kitchenTypes;
        this.rating = rating;
    }

    public RestaurantDTO(Integer id, String restaurantName, String pictureId, String street) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.pictureId = pictureId;
        this.street = street;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setKitchenTypes(Set<KitchenType> kitchenTypes) {
        this.kitchenTypes = kitchenTypes;
    }

    public Integer getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getStreet() {
        return street;
    }

    public Double getRating() {
        return rating;
    }

    public Set<KitchenType> getKitchenTypes() {
        return kitchenTypes;
    }



    @Override
    public String toString() {
        return "RestaurantDTO{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", pictureId='" + pictureId + '\'' +
                ", street='" + street + '\'' +
                ", rating=" + rating +
                ", kitchenTypes=" + kitchenTypes +
                '}';
    }
}
