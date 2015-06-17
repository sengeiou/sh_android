package com.shootr.android.data.entity;

public class FavoriteEntity extends Synchronized {

    private String idEvent;
    private Integer order;

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
