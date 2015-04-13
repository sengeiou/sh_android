package com.shootr.android.data.entity;

public class ShotEntity extends Synchronized{

    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_TRIGGER_SYNC = 1;
    public static final int TYPE_TRIGGER_SYNC_NOT_SHOW = 2;

    private Long idShot;
    private Long idUser;
    private String comment;
    private String image;
    private Long idEvent;
    private String eventTag;
    private String eventTitle;
    private Integer type;

    private Long idShotParent;
    private Long idUserParent;
    private String userNameParent;

    private UserEntity mUserObject;

    public Long getIdShot() {
        return idShot;
    }

    public void setIdShot(Long idShot) {
        this.idShot = idShot;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserEntity getUser() {
        return mUserObject;
    }

    public void setUser(UserEntity mUser) {
        this.mUserObject = mUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
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

    public Long getIdShotParent() {
        return idShotParent;
    }

    public void setIdShotParent(Long idShotParent) {
        this.idShotParent = idShotParent;
    }

    public Long getIdUserParent() {
        return idUserParent;
    }

    public void setIdUserParent(Long idUserParent) {
        this.idUserParent = idUserParent;
    }

    public String getUserNameParent() {
        return userNameParent;
    }

    public void setUserNameParent(String userNameParent) {
        this.userNameParent = userNameParent;
    }
}
