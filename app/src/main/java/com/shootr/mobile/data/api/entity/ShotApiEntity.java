package com.shootr.mobile.data.api.entity;

import java.util.List;

public class ShotApiEntity {

    private String idShot;
    private String comment;
    private String image;
    private String idStream;
    private String streamTitle;

    private Integer niceCount;

    private String type;

    private String idShotParent;
    private String idUserParent;
    private String userNameParent;

    private String videoUrl;
    private String videoTitle;
    private Long videoDuration;

    private EmbedUserApiEntity user;
    private List<ShotApiEntity> replies;
    private ShotApiEntity parent;

    private List<ShotApiEntity> parents;
    private Long birth;
    private Long modified;
    private Integer revision;
    private Long profileHidden;

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

    public List<ShotApiEntity> getReplies() {
        return replies;
    }

    public void setReplies(List<ShotApiEntity> replies) {
        this.replies = replies;
    }

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public ShotApiEntity getParent() {
        return parent;
    }

    public void setParent(ShotApiEntity parent) {
        this.parent = parent;
    }

    public Long getProfileHidden() {
        return profileHidden;
    }

    public void setProfileHidden(Long profileHidden) {
        this.profileHidden = profileHidden;
    }

    public List<ShotApiEntity> getParents() {
        return parents;
    }

    public void setParents(List<ShotApiEntity> parents) {
        this.parents = parents;
    }
}
