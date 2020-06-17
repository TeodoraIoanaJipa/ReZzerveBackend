package com.teo.foodzzzbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Indexed
@Table(name = "restaurants", schema = "dbo")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer id;

    @Column(name = "name", columnDefinition = "NVARCHAR")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @SortableField
    @Size(max = 30)
    private String restaurantName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private User manager;

    @Column(name = "description", columnDefinition = "NVARCHAR")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Size(max = 800)
    private String description;

    @Column(name = "opening_time", columnDefinition = "NVARCHAR")
    private String opensAt;

    @Column(name = "closing_time", columnDefinition = "NVARCHAR")
    private String closesAt;

    @Column(name = "price_category", columnDefinition = "NVARCHAR")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String price;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "kitchen_restaurant",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "kitchen_id")}
    )
    @IndexedEmbedded(depth = 2)
    private Set<KitchenType> kitchenTypes = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "local_restaurant",
            joinColumns = {@JoinColumn(name = "restaurant_id")},
            inverseJoinColumns = {@JoinColumn(name = "local_type_id")}
    )
    @IndexedEmbedded(depth = 1)
    private Set<LocalType> localTypes = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    @IndexedEmbedded(depth = 1)
    private Address address;

    @OneToMany(mappedBy = "restaurant")
    @IndexedEmbedded(depth = 1)
    private List<Images> images;

    @OneToMany(mappedBy = "restaurant")
    @IndexedEmbedded(depth = 1)
    private List<Tag> tags;

    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    private Integer width;

    private Integer height;


    @Transient
    private Double rating;

    public Restaurant(Integer id, String restaurantName, String pictureId, String description,
                      String opensAt, String closesAt, String price) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.description = description;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
        this.price = price;
    }

    public Restaurant() {
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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
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



    public Set<LocalType> getLocalTypes() {
        return localTypes;
    }

    public void setLocalTypes(Set<LocalType> localTypes) {
        this.localTypes = localTypes;
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

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "id =" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", manager=" + manager +
                ", description='" + description + '\'' +
                ", opensAt='" + opensAt + '\'' +
                ", rating=" + rating +
                '}';
    }
}
