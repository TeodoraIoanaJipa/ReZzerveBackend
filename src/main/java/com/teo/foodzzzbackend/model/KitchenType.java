package com.teo.foodzzzbackend.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "kitchen_types", schema = "dbo")
@Indexed
public class KitchenType implements Serializable {
    @Column(name = "kitchen_type", columnDefinition = "NVARCHAR")
    @Field
    private String kitchenName;

    @Id
    @Column(name = "type_id")
    private int id;

    @ManyToMany(mappedBy = "kitchenTypes")
    private Set<Restaurant> restaurants = new HashSet<>();

    public String getKitchenName() {
        return kitchenName;
    }

    public void setKitchenName(String kitchenName) {
        this.kitchenName = kitchenName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public KitchenType() {
    }

    @Override
    public String toString() {
        return "KitchenType{" +
                "kitchenName='" + kitchenName + '\'' +
                ", id=" + id +
                '}';
    }
}
