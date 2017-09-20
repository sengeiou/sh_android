package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.StreamEntity;

public class FavoriteApiEntity {

    private String idUser;
    private String idStream;
    private Integer order;

    private StreamEntity stream;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUsers) {
        this.idUser = idUsers;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStreams) {
        this.idStream = idStreams;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public StreamEntity getStream() {
        return stream;
    }

    public void setStream(StreamEntity stream) {
        this.stream = stream;
    }

}
