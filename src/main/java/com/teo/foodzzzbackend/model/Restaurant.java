package com.teo.foodzzzbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Indexed
@Table(name = "restaurants", schema = "dbo")
public class Restaurant {
    @Id
    @Column(name = "restaurant_id")
    private int id;

    @Column(name = "name", columnDefinition = "NVARCHAR")
    @Field
    private String restaurantName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User manager;

    @Column(name = "picture_id", columnDefinition = "NVARCHAR")
    private String pictureId;

    @Column(name = "description", columnDefinition = "NVARCHAR")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(name = "opening_time", columnDefinition = "NVARCHAR")
    private String opensAt;

    @Column(name = "closing_time", columnDefinition = "NVARCHAR")
    private String closesAt;

    @Column(name = "price_category", columnDefinition = "NVARCHAR")
    @Field
    private String price;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "kitchen_restaurant",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "kitchen_id")}
    )
    @IndexedEmbedded(depth = 1)
    private Set<KitchenType> kitchenTypes = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "local_type_id", referencedColumnName = "type_id")
    @IndexedEmbedded(depth = 1)
    private LocalType localType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @IndexedEmbedded(depth = 1)
    private Address address;

    @OneToMany(mappedBy = "restaurant")
    @IndexedEmbedded(depth = 1)
    private List<Tag> tags;

    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    @Transient
    private Double rating;

    public Restaurant(int id, String restaurantName, String pictureId, String description,
                      String opensAt, String closesAt, String price) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.pictureId = pictureId;
        this.description = description;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
        this.price = price;
    }

    public Restaurant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
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

    public LocalType getLocalType() {
        return localType;
    }

    public void setLocalType(LocalType localType) {
        this.localType = localType;
    }

    public Set<KitchenType> getKitchenTypes() {
        return kitchenTypes;
    }

    public void setKitchenTypes(Set<KitchenType> kitchenTypes) {
        this.kitchenTypes = kitchenTypes;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", manager=" + manager +
                ", pictureId='" + pictureId + '\'' +
                ", description='" + description + '\'' +
                ", opensAt='" + opensAt + '\'' +
                ", closesAt='" + closesAt + '\'' +
                ", price='" + price + '\'' +
                ", kitchenTypes=" + kitchenTypes +
                ", localType=" + localType +
                ", address=" + address +
                ", rating=" + rating +
                '}';
    }
}
