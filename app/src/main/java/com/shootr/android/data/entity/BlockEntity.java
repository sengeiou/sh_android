package com.shootr.android.data.entity;

public class BlockEntity extends Synchronized {

    private String idUser;
    private String idBlockedUser;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdBlockedUser() {
        return idBlockedUser;
    }

    public void setIdBlockedUser(String idBlockedUser) {
        this.idBlockedUser = idBlockedUser;
    }
}
