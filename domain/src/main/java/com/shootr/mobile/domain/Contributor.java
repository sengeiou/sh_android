package com.shootr.mobile.domain;

public class Contributor {

    private String idUser;
    private String idStream;
    private String idContributor;
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
