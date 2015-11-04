package com.shootr.mobile.ui.model;

import java.util.Date;

public class ActivityModel {

    private String idActivity;
    private String idUser;
    private String idTargetUser;
    private String username;
    private String userPhoto;
    private String idStream;
    private String streamTitle;
    private String streamShortTitle;
    private String comment;
    private String type;
    private Date publishDate;
    private ShotModel shot;
    private String idStreamAuthor;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public String getIdTargetUser() {
        return idTargetUser;
    }

    public void setIdTargetUser(String idTargetUser) {
        this.idTargetUser = idTargetUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getStreamShortTitle() {
        return streamShortTitle;
    }

    public void setStreamShortTitle(String streamShortTitle) {
        this.streamShortTitle = streamShortTitle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public ShotModel getShot() {
        return shot;
    }

    public void setShot(ShotModel shot) {
        this.shot = shot;
    }

    public String getIdStreamAuthor() {
        return idStreamAuthor;
    }

    public void setIdStreamAuthor(String idStreamAuthor) {
        this.idStreamAuthor = idStreamAuthor;
    }
}