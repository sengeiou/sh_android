package com.shootr.android.data.entity;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.utils.Preconditions;

public class ActivityEntity extends Synchronized {

    private String idActivity;
    private String idUser;
    private String idTargetUser;
    private String username;
    private String idStream;
    private String streamTitle;
    private String streamShortTitle;
    private String comment;
    private String type;
    private String userPhoto;
    private String idShot;
    private Shot shotForMapping;
    private ActivityEntityStreamInfo activityEntityStreamInfo;

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

    public String getIdTargetUser() {
        return idTargetUser;
    }

    public void setIdTargetUser(String idTargetUser) {
        this.idTargetUser = idTargetUser;
    }

    public ActivityEntityStreamInfo getActivityEntityStreamInfo() {
        return activityEntityStreamInfo;
    }

    public void setActivityEntityStreamInfo(ActivityEntityStreamInfo activityEntityStreamInfo) {
        this.activityEntityStreamInfo = activityEntityStreamInfo;
    }

    public static class ActivityEntityStreamInfo {

        private String idStream;
        private String streamTitle;
        private String streamShortTitle;
        private String idUser;

        public String getStreamShortTitle() {
            return streamShortTitle;
        }

        public void setStreamShortTitle(String streamShortTitle) {
            this.streamShortTitle = streamShortTitle;
        }

        public String getStreamTitle() {
            return streamTitle;
        }

        public void setStreamTitle(String streamTitle) {
            this.streamTitle = streamTitle;
        }

        public String getIdStream() {
            return idStream;
        }

        public void setIdStream(String idStream) {
            this.idStream = idStream;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActivityEntityStreamInfo)) return false;

            ActivityEntityStreamInfo that = (ActivityEntityStreamInfo) o;

            if (idStream != null ? !idStream.equals(that.idStream) : that.idStream != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return idStream != null ? idStream.hashCode() : 0;
        }

        @Override public String toString() {
            return "ActivityStreamInfo{" +
              "streamTitle='" + streamTitle + '\'' +
              ", idStream=" + idStream +
              '}';
        }

        public String getIdUser() {
            return idUser;
        }

        public void setIdUser(String idUser) {
            this.idUser = idUser;
        }
    }
}
