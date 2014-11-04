package gm.mobi.android.ui.model;

import java.io.Serializable;

public class UserWatchingModel implements Serializable {

    private Long idUser;
    private Long favoriteTeamId;
    private String userName;
    private String photo;
    private boolean live;

    private String status; //TODO cambiar por valor

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
