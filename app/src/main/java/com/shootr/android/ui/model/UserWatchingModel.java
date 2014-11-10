package com.shootr.android.ui.model;

import java.io.Serializable;

public class UserWatchingModel implements Serializable {

    private Long idUser;
    private Long favoriteTeamId;
    private String userName;
    private String photo;
    private boolean live;

    private boolean watching;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getFavoriteTeamId() {
        return favoriteTeamId;
    }

    public void setFavoriteTeamId(Long favoriteTeamId) {
        this.favoriteTeamId = favoriteTeamId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isWatching() {
        return watching;
    }

    public void setWatching(boolean watching) {
        this.watching = watching;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UserWatchingModel)) return false;
        UserWatchingModel that = (UserWatchingModel) o;
        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null) return false;
        return true;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
