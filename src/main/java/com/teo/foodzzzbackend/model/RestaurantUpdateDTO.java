package com.teo.foodzzzbackend.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantUpdateDTO {
    private Integer id;
    private String restaurantName;
    private Long managerId;
    private String pictureId;
    private String description;
    private String opensAt;
    private String closesAt;
    private String price;
    private Set<KitchenType> kitchenTypes = new HashSet<>();
    private Set<LocalType> localTypes = new HashSet<>();
    private Address address;
    private List<Tag> tags;

    public RestaurantUpdateDTO() {
    }

    public RestaurantUpdateDTO(Integer id, String restaurantName, Long managerId, String pictureId, String description, String opensAt, String closesAt, String price, Set<KitchenType> kitchenTypes, Set<LocalType> localTypes, List<Tag> tags) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.managerId = managerId;
        this.pictureId = pictureId;
        this.description = description;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
        this.price = price;
        this.kitchenTypes = kitchenTypes;
        this.localTypes = localTypes;
        this.tags = tags;
    }

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

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
