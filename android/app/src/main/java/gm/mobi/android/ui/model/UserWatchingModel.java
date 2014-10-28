package gm.mobi.android.ui.model;

import java.io.Serializable;

public class UserWatchingModel implements Serializable {

    private Long idUser;
    private String favoriteTeamName;
    private Long favoriteTeamId;
    private String userName;
    private String name;
    private String photo;

    private String status; //TODO cambiar por valor

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getFavoriteTeamName() {
        return favoriteTeamName;
    }

    public void setFavoriteTeamName(String favoriteTeamName) {
        this.favoriteTeamName = favoriteTeamName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
