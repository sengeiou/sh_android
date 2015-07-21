package com.shootr.android.data.api.entity;

import com.shootr.android.data.entity.StreamEntity;

public class FavoriteApiEntity {

    private String idUser;
    private String idEvent;
    private Integer order;

    private StreamEntity event;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

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

    public StreamEntity getEvent() {
        return event;
    }

    public void setEvent(StreamEntity event) {
        this.event = event;
    }

}
