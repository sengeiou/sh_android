package com.shootr.android.data.entity;

public class ShotEntity extends Synchronized{

    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_TRIGGER_SYNC = 1;
    public static final int TYPE_TRIGGER_SYNC_NOT_SHOW = 2;

    private String idShot;
    private String idUser;
    private String username;
    private String comment;
    private String image;
    private String idEvent;
    private String eventTag;
    private String eventTitle;
    private Integer type;

    private String idShotParent;
    private String idUserParent;
    private String userNameParent;

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
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

    public String getIdShotParent() {
        return idShotParent;
    }

    public void setIdShotParent(String idShotParent) {
        this.idShotParent = idShotParent;
    }

    public String getIdUserParent() {
        return idUserParent;
    }

    public void setIdUserParent(String idUserParent) {
        this.idUserParent = idUserParent;
    }

    public String getUserNameParent() {
        return userNameParent;
    }

    public void setUserNameParent(String userNameParent) {
        this.userNameParent = userNameParent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
