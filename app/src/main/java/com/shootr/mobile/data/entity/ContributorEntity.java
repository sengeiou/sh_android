package com.shootr.mobile.data.entity;

public class ContributorEntity {

    private String idUser;
    private String idStream;
    private String idContributor;
    private UserEntity user;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getIdContributor() {
        return idContributor;
    }

    public void setIdContributor(String idContributor) {
        this.idContributor = idContributor;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
