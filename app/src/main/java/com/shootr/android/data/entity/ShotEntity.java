package com.shootr.android.data.entity;

public class ShotEntity extends Synchronized{

    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_WATCH = 1;
    public static final int TYPE_WATCH_NEGATIVE = 2;

    private Long idShot;
    private Long idUser;
    private String comment;
    private String image;
    private Long idEvent;
    private Integer type;

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
}
