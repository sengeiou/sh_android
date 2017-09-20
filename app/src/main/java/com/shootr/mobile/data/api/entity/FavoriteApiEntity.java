package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.StreamEntity;

public class FavoriteApiEntity {

    private String idUsers;
    private String idStreams;
    private Integer order;

    private StreamEntity stream;

    public String getIdUser() {
        return idUsers;
    }

    public void setIdUser(String idUsers) {
        this.idUsers = idUsers;
    }

    public String getIdStream() {
        return idStreams;
    }

    public void setIdStream(String idStreams) {
        this.idStreams = idStreams;
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
