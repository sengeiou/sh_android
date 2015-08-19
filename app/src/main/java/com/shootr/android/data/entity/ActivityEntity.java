package com.shootr.android.data.entity;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.utils.Preconditions;

public class ActivityEntity extends Synchronized {

    private String idActivity;
    private String idUser;
    private String username;
    private String idStream;
    private String streamTitle;
    private String streamTag;
    private String comment;
    private String type;
    private String userPhoto;
    private String idShot;
    private Shot shotForMapping;

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

    public String getStreamTag() {
        return streamTag;
    }

    public void setStreamTag(String streamTag) {
        this.streamTag = streamTag;
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

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
    }

    public Shot getShotForMapping() {
        return shotForMapping;
    }

    public void setShotForMapping(Shot shotForMapping) {
        Preconditions.checkState(idShot != null, "Can't set ShotForMapping to an activity without idShot. Activity id: "+idActivity);
        this.shotForMapping = shotForMapping;
    }
}
