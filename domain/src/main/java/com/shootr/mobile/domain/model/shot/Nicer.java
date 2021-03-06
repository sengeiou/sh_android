package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.user.User;

public class Nicer {

    private String idUser;
    private String idShot;
    private String userName;
    private String idNice;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdNice() {
        return idNice;
    }

    public void setIdNice(String idNice) {
        this.idNice = idNice;
    }
}
