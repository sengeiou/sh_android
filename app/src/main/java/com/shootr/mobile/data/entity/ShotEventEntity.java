package com.shootr.mobile.data.entity;

public class ShotEventEntity {

    private String id;
    private String type;
    private Long timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdShot() {
        return id;
    }

    public void setIdShot(String idShot) {
        this.id = idShot;
    }
}
