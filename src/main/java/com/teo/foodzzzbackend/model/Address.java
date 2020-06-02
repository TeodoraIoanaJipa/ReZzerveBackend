package com.teo.foodzzzbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "address", schema = "dbo")
public class Address implements Serializable {
    @Id
    @Column(name = "address_id")
    private int id;

    @Column(name = "street", columnDefinition = "NVARCHAR")
    @Field
    private String street;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    public Address() {
    }

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

}
