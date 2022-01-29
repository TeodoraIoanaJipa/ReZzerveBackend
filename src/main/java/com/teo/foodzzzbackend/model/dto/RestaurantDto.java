package com.teo.foodzzzbackend.model.dto;

import com.teo.foodzzzbackend.model.*;

import java.util.List;
import java.util.Set;

public class RestaurantDto {
    private Integer id;
    private String restaurantName;
    private String description;
    private String opensAt;
    private String closesAt;
    private String price;
    private Set<KitchenType> kitchenTypes;
    private Set<LocalType> localTypes;
    private Address address;
    private List<Images> images;
    private List<Review> reviews;
    private List<Tag> tags;
    private List<TableForm> tables;
    private Integer width;
    private Integer height;
    private Double rating;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpensAt() {
        return opensAt;
    }

    public void setOpensAt(String opensAt) {
        this.opensAt = opensAt;
    }

    public String getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(String closesAt) {
        this.closesAt = closesAt;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Set<KitchenType> getKitchenTypes() {
        return kitchenTypes;
    }

    public void setKitchenTypes(Set<KitchenType> kitchenTypes) {
        this.kitchenTypes = kitchenTypes;
    }

    public Set<LocalType> getLocalTypes() {
        return localTypes;
    }

    public void setLocalTypes(Set<LocalType> localTypes) {
        this.localTypes = localTypes;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<TableForm> getTables() {
        return tables;
    }

    public void setTables(List<TableForm> tables) {
        this.tables = tables;
    }
}
