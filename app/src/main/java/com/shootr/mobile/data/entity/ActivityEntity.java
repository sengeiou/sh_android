package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.utils.Preconditions;

public class ActivityEntity extends Synchronized {

    private String idActivity;
    private String idUser;
    private String idTargetUser;
    private String username;
    private String name;
    private String targetName;
    private String idStream;
    private String streamTitle;
    private String streamPhoto;
    private String comment;
    private String type;
    private String userPhoto;
    private String targetUserPhoto;
    private String idShot;
    private Shot shotForMapping;
    private String idStreamAuthor;
    private String idPoll;
    private String pollQuestion;
    private String pollOptionText;
    private boolean strategic;
    private boolean isVerified;
    private boolean isFollowing;
    private String targetUsername;

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
        Preconditions.checkState(idShot != null,
            "Can't set ShotForMapping to an activity without idShot. Activity id: " + idActivity);
        this.shotForMapping = shotForMapping;
    }

    public String getIdTargetUser() {
        return idTargetUser;
    }

    public void setIdTargetUser(String idTargetUser) {
        this.idTargetUser = idTargetUser;
    }

    public String getIdStreamAuthor() {
        return idStreamAuthor;
    }

    public void setIdStreamAuthor(String idStreamAuthor) {
        this.idStreamAuthor = idStreamAuthor;
    }

    public String getIdPoll() {
        return idPoll;
    }

    public void setIdPoll(String idPoll) {
        this.idPoll = idPoll;
    }

    public String getPollQuestion() {
        return pollQuestion;
    }

    public void setPollQuestion(String pollQuestion) {
        this.pollQuestion = pollQuestion;
    }

    public String getPollOptionText() {
        return pollOptionText;
    }

    public void setPollOptionText(String pollOptionText) {
        this.pollOptionText = pollOptionText;
    }

    public boolean isStrategic() {
        return strategic;
    }

    public void setStrategic(boolean strategic) {
        this.strategic = strategic;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getStreamPhoto() {
        return streamPhoto;
    }

    public void setStreamPhoto(String streamPhoto) {
        this.streamPhoto = streamPhoto;
    }

    public String getTargetUserPhoto() {
        return targetUserPhoto;
    }

    public void setTargetUserPhoto(String targetUserPhoto) {
        this.targetUserPhoto = targetUserPhoto;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }
}
