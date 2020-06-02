package com.teo.foodzzzbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name="tag_restaurant",schema="dbo")
public class Tag {
    @Id
    @Column(name = "tag_id")
    private int tagId;

    @Column(name = "tag_name", columnDefinition = "NVARCHAR")
    @Field
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private Restaurant restaurant;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


}
