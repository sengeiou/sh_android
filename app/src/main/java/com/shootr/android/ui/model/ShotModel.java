package com.shootr.android.ui.model;

import java.io.Serializable;
import java.util.Date;

public class ShotModel implements Serializable{

    //Shot attributes
    private String idShot;
    private String comment;
    private String image;
    private Date csysBirth;
    //User attributes
    private String idUser;
    private String userName;
    private String photo;
    private String eventTag;
    private String eventTitle;
    private String type;

    private String replyUsername;
    private String parentShotId;

    private String videoUrl;
    private String videoTitle;
    private String videoDuration;

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
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

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public boolean isReply() {
        return replyUsername != null;
    }

    public String getParentShotId() {
        return parentShotId;
    }

    public void setParentShotId(String parentShotId) {
        this.parentShotId = parentShotId;
    }

    public boolean hasVideo() {
        return videoUrl != null;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }
}
