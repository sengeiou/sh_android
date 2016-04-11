package com.shootr.mobile.domain;

import java.util.Comparator;
import java.util.Date;

public class Shot {

    private String idShot;
    private String comment;
    private String image;
    private ShotUserInfo userInfo;
    private ShotStreamInfo streamInfo;
    private Date publishDate;
    private Long idQueue;

    private String parentShotId;
    private String parentShotUserId;
    private String parentShotUsername;

    private String videoUrl;
    private String videoTitle;
    private Long videoDuration;

    private String type;
    private Integer niceCount;
    private Boolean isMarkedAsNice;
    private Long profileHidden;
    private EntityMetadata metadata;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ShotUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(ShotUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ShotStreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(ShotStreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    public String getParentShotId() {
        return parentShotId;
    }

    public void setParentShotId(String parentShotId) {
        this.parentShotId = parentShotId;
    }

    public String getParentShotUserId() {
        return parentShotUserId;
    }

    public void setParentShotUserId(String parentShotUserId) {
        this.parentShotUserId = parentShotUserId;
    }

    public String getParentShotUsername() {
        return parentShotUsername;
    }

    public void setParentShotUsername(String parentShotUsername) {
        this.parentShotUsername = parentShotUsername;
    }

    public boolean hasVideoEmbed() {
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

    public Long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(EntityMetadata metadata) {
        this.metadata = metadata;
    }

    public Boolean isMarkedAsNice() {
        return isMarkedAsNice;
    }

    public void setIsMarkedAsNice(Boolean isMarkedAsNice) {
        this.isMarkedAsNice = isMarkedAsNice;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shot)) return false;

        Shot shot = (Shot) o;

        if (idShot != null ? !idShot.equals(shot.idShot) : shot.idShot != null) return false;
        if (comment != null ? !comment.equals(shot.comment) : shot.comment != null) return false;
        if (image != null ? !image.equals(shot.image) : shot.image != null) return false;
        if (userInfo != null ? !userInfo.equals(shot.userInfo) : shot.userInfo != null) return false;
        if (streamInfo != null ? !streamInfo.equals(shot.streamInfo) : shot.streamInfo != null) return false;
        if (publishDate != null ? !publishDate.equals(shot.publishDate) : shot.publishDate != null) return false;
        if (idQueue != null ? !idQueue.equals(shot.idQueue) : shot.idQueue != null) return false;
        if (parentShotId != null ? !parentShotId.equals(shot.parentShotId) : shot.parentShotId != null) {
            return false;
        }
        if (parentShotUserId != null ? !parentShotUserId.equals(shot.parentShotUserId)
          : shot.parentShotUserId != null) {
            return false;
        }
        if (parentShotUsername != null ? !parentShotUsername.equals(shot.parentShotUsername)
          : shot.parentShotUsername != null) {
            return false;
        }
        if (videoUrl != null ? !videoUrl.equals(shot.videoUrl) : shot.videoUrl != null) return false;
        if (videoTitle != null ? !videoTitle.equals(shot.videoTitle) : shot.videoTitle != null) return false;
        if (videoDuration != null ? !videoDuration.equals(shot.videoDuration) : shot.videoDuration != null) {
            return false;
        }
        if (type != null ? !type.equals(shot.type) : shot.type != null) return false;
        if (niceCount != null ? !niceCount.equals(shot.niceCount) : shot.niceCount != null) return false;
        if (isMarkedAsNice != null ? !isMarkedAsNice.equals(shot.isMarkedAsNice) : shot.isMarkedAsNice != null) {
            return false;
        }
        return !(metadata != null ? !metadata.equals(shot.metadata) : shot.metadata != null);
    }

    @Override public int hashCode() {
        int result = idShot != null ? idShot.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
        result = 31 * result + (streamInfo != null ? streamInfo.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (idQueue != null ? idQueue.hashCode() : 0);
        result = 31 * result + (parentShotId != null ? parentShotId.hashCode() : 0);
        result = 31 * result + (parentShotUserId != null ? parentShotUserId.hashCode() : 0);
        result = 31 * result + (parentShotUsername != null ? parentShotUsername.hashCode() : 0);
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (videoTitle != null ? videoTitle.hashCode() : 0);
        result = 31 * result + (videoDuration != null ? videoDuration.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (niceCount != null ? niceCount.hashCode() : 0);
        result = 31 * result + (isMarkedAsNice != null ? isMarkedAsNice.hashCode() : 0);
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Shot{" +
          "idShot=" + idShot +
          ", comment='" + comment + '\'' +
          ", image='" + image + '\'' +
          ", niceCount='" + niceCount + '\'' +
          ", publishDate=" + publishDate +
          '}';
    }

    public Long getProfileHidden() {
        return profileHidden;
    }

    public void setProfileHidden(Long profileHidden) {
        this.profileHidden = profileHidden;
    }

    public static class ShotStreamInfo {

        private String idStream;
        private String streamTitle;
        private String streamShortTitle;

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

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShotStreamInfo)) return false;

            ShotStreamInfo that = (ShotStreamInfo) o;

            if (idStream != null ? !idStream.equals(that.idStream) : that.idStream != null) return false;

            return true;
        }

        @Override public int hashCode() {
            return idStream != null ? idStream.hashCode() : 0;
        }

        @Override public String toString() {
            return "ShotStreamInfo{" +
              "streamTitle='" + streamTitle + '\'' +
              ", idStream=" + idStream +
              '}';
        }
    }

    public static class ShotUserInfo {

        private String idUser;
        private String username;
        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getIdUser() {
            return idUser;
        }

        public void setIdUser(String idUser) {
            this.idUser = idUser;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShotUserInfo)) return false;

            ShotUserInfo that = (ShotUserInfo) o;

            if (!idUser.equals(that.idUser)) return false;

            return true;
        }

        @Override public int hashCode() {
            return idUser.hashCode();
        }

        @Override public String toString() {
            return "ShotUserInfo{" +
              "username='" + username + '\'' +
              ", idUser=" + idUser +
              '}';
        }
    }

    public static class NewerAboveComparator implements Comparator<Shot> {

        @Override public int compare(Shot s1, Shot s2) {
            return s2.getPublishDate().compareTo(s1.getPublishDate());
        }
    }

    public static class NewerBelowComparator implements Comparator<Shot> {

        @Override public int compare(Shot s1, Shot s2) {
            return s1.getPublishDate().compareTo(s2.getPublishDate());
        }
    }
}