package com.teo.foodzzzbackend.model;


public class RestaurantDTO {
    private Integer id;
    private String restaurantName;
    private String street;
    private Double rating;
    private Long imageId;
    private KitchenType kitchenType;

    public RestaurantDTO() {
    }

    public RestaurantDTO(Integer id, String restaurantName, String street, Long imageId, Double rating) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.street = street;
        this.imageId = imageId;
        this.rating = rating;
    }


    public RestaurantDTO(Integer id, String restaurantName, String street, Long imageId) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.street = street;
        this.imageId = imageId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }


    public void setStreet(String street) {
        this.street = street;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setKitchenType(KitchenType kitchenType) {
        this.kitchenType = kitchenType;
    }

    public Integer getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getStreet() {
        return street;
    }

    public Double getRating() {
        return rating;
    }

    public KitchenType getKitchenType() {
        return kitchenType;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "RestaurantDTO{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", rating=" + rating +
                '}';
    }
}
