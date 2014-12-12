package com.shootr.android.ui.model;

import java.util.Date;

public class ShotModel {

    //Shot attributes
    private Long idShot;
    private String comment;
    private String image;
    private Date csysBirth;
    //User attributes
    private Long idUser;
    private String userName;
    private String photo;

    public Long getIdShot() {
        return idShot;
    }

    public void setIdShot(Long idShot) {
        this.idShot = idShot;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCsysBirth() {
        return csysBirth;
    }

    public void setCsysBirth(Date csysBirth) {
        this.csysBirth = csysBirth;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
