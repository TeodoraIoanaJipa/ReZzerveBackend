package com.teo.foodzzzbackend.model;

public enum ReservationConfirmationStatus {
    IN_PROGRESS(0),
    CONFIRMED(1),
    DECLINED(2);

    private String type;
    private Integer id;

    ReservationConfirmationStatus(Integer id) {
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
