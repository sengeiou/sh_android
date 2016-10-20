package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ShotModel implements Comparable<ShotModel>, Serializable {

    private String idShot;
    private String comment;
    private ShotImageModel image;
    private Date birth;
    private List<String> nicers;
    private String idUser;
    private String userName;
    private String photo;
    private String streamId;
    private String streamTitle;
    private String replyUsername;
    private String parentShotId;
    private String videoUrl;
    private String videoTitle;
    private String videoDuration;
    private Integer niceCount;
    private Boolean isMarkedAsNice;
    private Long hide;
    private Long replyCount;
    private Long views;
    private Long linkClickCount;
    private Long reshootCount;
    private String type;
    private String ctaCaption;
    private String ctaButtonLink;
    private String ctaButtonText;
    private Long promoted;

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

    public ShotImageModel getImage() {
        return image;
    }

    public void setImage(ShotImageModel image) {
        this.image = image;
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

    public List<String> getNicers() {
        return nicers;
    }

    public void setNicers(List<String> nicers) {
        this.nicers = nicers;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLinkClickCount() {
        return linkClickCount;
    }

    public void setLinkClickCount(Long linkClickCount) {
        this.linkClickCount = linkClickCount;
    }

    public Long getReshootCount() {
        return reshootCount;
    }

    public void setReshootCount(Long reshootCount) {
        this.reshootCount = reshootCount;
    }

    public String getCtaCaption() {
        return ctaCaption;
    }

    public void setCtaCaption(String ctaCaption) {
        this.ctaCaption = ctaCaption;
    }

    public String getCtaButtonLink() {
        return ctaButtonLink;
    }

    public void setCtaButtonLink(String ctaButtonLink) {
        this.ctaButtonLink = ctaButtonLink;
    }

    public String getCtaButtonText() {
        return ctaButtonText;
    }

    public void setCtaButtonText(String ctaButtonText) {
        this.ctaButtonText = ctaButtonText;
    }

    public Long getPromoted() {
        return promoted;
    }

    public void setPromoted(Long promoted) {
        this.promoted = promoted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override public int compareTo(ShotModel shotModel) {
        return this.getBirth().getTime() > shotModel.getBirth().getTime() ? 1 : 0;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShotModel)) return false;

        ShotModel shotModel = (ShotModel) o;

        if (idShot != null ? !idShot.equals(shotModel.idShot) : shotModel.idShot != null)
            return false;
        if (nicers != null ? !nicers.equals(shotModel.nicers) : shotModel.nicers != null)
            return false;
        if (streamTitle != null ? !streamTitle.equals(shotModel.streamTitle)
            : shotModel.streamTitle != null) {
            return false;
        }
        if (niceCount != null ? !niceCount.equals(shotModel.niceCount)
            : shotModel.niceCount != null) {
            return false;
        }
        if (isMarkedAsNice != null ? !isMarkedAsNice.equals(shotModel.isMarkedAsNice)
            : shotModel.isMarkedAsNice != null) {
            return false;
        }
        if (hide != null ? !hide.equals(shotModel.hide) : shotModel.hide != null) return false;
        return replyCount != null ? replyCount.equals(shotModel.replyCount)
            : shotModel.replyCount == null;
    }

    @Override public int hashCode() {
        int result = idShot != null ? idShot.hashCode() : 0;
        result = 31 * result + (nicers != null ? nicers.hashCode() : 0);
        result = 31 * result + (streamTitle != null ? streamTitle.hashCode() : 0);
        result = 31 * result + (niceCount != null ? niceCount.hashCode() : 0);
        result = 31 * result + (isMarkedAsNice != null ? isMarkedAsNice.hashCode() : 0);
        result = 31 * result + (hide != null ? hide.hashCode() : 0);
        result = 31 * result + (replyCount != null ? replyCount.hashCode() : 0);
        return result;
    }
}
