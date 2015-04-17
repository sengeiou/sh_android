package com.shootr.android.ui.model;

import java.io.Serializable;
import java.util.Date;

public class ShotModel implements Serializable{

    //Shot attributes
    private Long idShot;
    private String comment;
    private String image;
    private Date csysBirth;
    //User attributes
    private Long idUser;
    private String userName;
    private String photo;
    private String eventTag;
    private String eventTitle;
    private Integer type;

    private String replyUsername;
    private Long parentShotId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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

    public Long getParentShotId() {
        return parentShotId;
    }

    public void setParentShotId(Long parentShotId) {
        this.parentShotId = parentShotId;
    }
}
