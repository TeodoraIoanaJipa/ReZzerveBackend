package com.teo.foodzzzbackend.model;

import org.hibernate.search.annotations.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "local_types", schema = "dbo")
public class LocalType implements Serializable {
    @Column(name = "local_type", columnDefinition = "NVARCHAR")
    @Field
    private String localType;

    @Id
    @Column(name = "type_id")
    private int id;

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
