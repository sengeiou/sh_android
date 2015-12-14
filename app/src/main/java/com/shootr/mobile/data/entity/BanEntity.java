package com.shootr.mobile.data.entity;

public class BanEntity {

    private String idUser;
    private String idBannedUser;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdBannedUser() {
        return idBannedUser;
    }

    public void setIdBannedUser(String idBlockedUser) {
        this.idBannedUser = idBlockedUser;
    }

}
