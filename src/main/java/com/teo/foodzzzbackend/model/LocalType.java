package com.teo.foodzzzbackend.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "local_types", schema = "dbo")
@Indexed
public class LocalType implements Serializable {
    @Column(name = "local_type", columnDefinition = "NVARCHAR")
    @Field
    private String localType;

    @Id
    @Column(name = "type_id")
    private int id;

    @ManyToMany(mappedBy = "localTypes")
    private Set<Restaurant> restaurants = new HashSet<>();

    public String getLocalType() {
        return localType;
    }

    public void setLocalType(String localType) {
        this.localType = localType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
