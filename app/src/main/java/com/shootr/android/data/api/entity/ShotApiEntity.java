package com.shootr.android.data.api.entity;

public class ShotApiEntity {

    private String idShot;
    private String comment;
    private String image;
    private String idEvent;
    private String eventTag;
    private String eventTitle;

    private String type;

    private String idShotParent;
    private String idUserParent;
    private String userNameParent;

    private String videoUrl;
    private String videoTitle;
    private Long videoDuration;

    private EmbedUserApiEntity user;

    private Long birth;
    private Long modified;
    private Integer revision;

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

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public EmbedUserApiEntity getUser() {
        return user;
    }

    public void setUser(EmbedUserApiEntity user) {
        this.user = user;
    }

    public Long getBirth() {
        return birth;
    }

    public void setBirth(Long birth) {
        this.birth = birth;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}
