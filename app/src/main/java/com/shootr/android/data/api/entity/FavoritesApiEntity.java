package com.shootr.android.data.api.entity;

public class FavoritesApiEntity {

    private String idStream;
    private Integer favorites;

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }
}
