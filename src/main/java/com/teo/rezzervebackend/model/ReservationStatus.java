package com.teo.foodzzzbackend.model;

public enum ReservationStatus {
    IN_PROGRESS(0),
    CANCELLED(1),
    ACCEPTED(2),
    DECLINED(3);

    private String type;
    private Integer id;

    ReservationStatus(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
