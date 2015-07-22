package com.shootr.android.data.entity;

public class FavoriteEntity extends LocalSynchronized {

    private String idStream;
    private Integer order;

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
