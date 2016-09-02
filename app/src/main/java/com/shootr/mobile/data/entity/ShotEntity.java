package com.shootr.mobile.data.entity;

public class ShotEntity extends Synchronized {

    private String idShot;
    private String idUser;
    private String username;
    private String userPhoto;
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
    private Long profileHidden;
    private Long replyCount;

    private Long linkClicks;
    private Long views;

    private Long imageWidth;
    private Long imageHeight;

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

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
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

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Integer getNiceCount() {
        return niceCount;
    }

    public void setNiceCount(Integer niceCount) {
        this.niceCount = niceCount;
    }

    public Long getProfileHidden() {
        return profileHidden;
    }

    public void setProfileHidden(Long profileHidden) {
        this.profileHidden = profileHidden;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Long getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLinkClicks() {
        return linkClicks;
    }

    public void setLinkClicks(Long linkClicks) {
        this.linkClicks = linkClicks;
    }
}
