package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.Date;

public class ShotModel implements Serializable{

    //Shot attributes
    private String idShot;
    private String comment;
    private String image;
    private Date birth;
    //User attributes
    private String idUser;
    private String userName;
    private String photo;
    private String streamId;
    private String streamShortTitle;
    private String streamTitle;

    private String replyUsername;
    private String parentShotId;

    private String videoUrl;
    private String videoTitle;
    private String videoDuration;

    private Integer niceCount;
    private Boolean isMarkedAsNice;

    private Long hide;

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

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
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

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public Boolean isMarkedAsNice() {
        return isMarkedAsNice;
    }

    public void setIsMarkedAsNice(Boolean isMarkedAsNice) {
        this.isMarkedAsNice = isMarkedAsNice;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public Long getHide() {
        return hide;
    }

    public void setHide(Long hide) {
        this.hide = hide;
    }
}
