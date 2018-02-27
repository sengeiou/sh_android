package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.messages.Message;
import java.util.Comparator;
import java.util.Date;

public class Shot extends BaseMessage {

    private String idShot;
    private ShotStreamInfo streamInfo;
    private Date publishDate;
    private Long idQueue;
    private String parentShotId;
    private String parentShotUserId;
    private String parentShotUsername;
    private String type;
    private Integer niceCount;
    private Long profileHidden;
    private Long replyCount;
    private Long linkClicks;
    private Long views;
    private Long reshootCount;
    private String ctaCaption;
    private String ctaButtonLink;
    private String ctaButtonText;
    private Long promoted;
    private boolean isPadding;
    private boolean isFromHolder;
    private boolean isFromContributor;
    private boolean niced;
    private boolean reshooted;
    private Date nicedTime;
    private Date reshootedTime;
    private String shareLink;

    public String getIdShot() {
        return idShot;
    }

    public void setIdShot(String idShot) {
        this.idShot = idShot;
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shot)) return false;

        Shot shot = (Shot) o;

        if (idShot != null ? !idShot.equals(shot.idShot) : shot.idShot != null) return false;
        if (getComment() != null ? !getComment().equals(shot.getComment())
            : shot.getComment() != null) {
            return false;
        }
        if (getImage() != null ? !getImage().equals(shot.getImage()) : shot.getImage() != null) {
            return false;
        }
        if (getUserInfo() != null ? !getUserInfo().equals(shot.getUserInfo())
            : shot.getUserInfo() != null) {
            return false;
        }
        if (streamInfo != null ? !streamInfo.equals(shot.streamInfo) : shot.streamInfo != null) {
            return false;
        }
        if (publishDate != null ? !publishDate.equals(shot.publishDate)
            : shot.publishDate != null) {
            return false;
        }
        if (getIdQueue() != null ? !getIdQueue().equals(shot.getIdQueue())
            : shot.getIdQueue() != null) {
            return false;
        }
        if (parentShotId != null ? !parentShotId.equals(shot.parentShotId)
            : shot.parentShotId != null) {
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
        if (getVideoUrl() != null ? !getVideoUrl().equals(shot.getVideoUrl())
            : shot.getVideoUrl() != null) {
            return false;
        }
        if (getVideoTitle() != null ? !getVideoTitle().equals(shot.getVideoTitle())
            : shot.getVideoTitle() != null) {
            return false;
        }
        if (getVideoDuration() != null ? !getVideoDuration().equals(shot.getVideoDuration())
            : shot.getVideoDuration() != null) {
            return false;
        }
        if (type != null ? !type.equals(shot.type) : shot.type != null) return false;
        if (niceCount != null ? !niceCount.equals(shot.niceCount) : shot.niceCount != null) return false;

        return !(getMetadata() != null ? !getMetadata().equals(shot.getMetadata()) : shot.getMetadata() != null);
    }

    @Override public int hashCode() {
        int result = idShot != null ? idShot.hashCode() : 0;
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        result = 31 * result + (getImage() != null ? getImage().hashCode() : 0);
        result = 31 * result + (getUserInfo() != null ? getUserInfo().hashCode() : 0);
        result = 31 * result + (streamInfo != null ? streamInfo.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (getIdQueue() != null ? getIdQueue().hashCode() : 0);
        result = 31 * result + (parentShotId != null ? parentShotId.hashCode() : 0);
        result = 31 * result + (parentShotUserId != null ? parentShotUserId.hashCode() : 0);
        result = 31 * result + (parentShotUsername != null ? parentShotUsername.hashCode() : 0);
        result = 31 * result + (getVideoUrl() != null ? getVideoUrl().hashCode() : 0);
        result = 31 * result + (getVideoTitle() != null ? getVideoTitle().hashCode() : 0);
        result = 31 * result + (getVideoDuration() != null ? getVideoDuration().hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (niceCount != null ? niceCount.hashCode() : 0);
        result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Shot{" +
          "idShot=" + idShot +
          ", comment='" + getComment() + '\'' +
          ", image='" + getImage() + '\'' +
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

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    @Override public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    @Override public String getResultType() {
        return "SHOT";
    }

    @Override public String getMessageType() {
        return Message.SHOT;
    }

    public static class ShotStreamInfo {

        private String idStream;
        private String streamTitle;

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

    public Long getLinkClicks() {
        return linkClicks;
    }

    public void setLinkClicks(Long linkClicks) {
        this.linkClicks = linkClicks;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
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

    public Long getPromoted() {
        return promoted;
    }

    public void setPromoted(Long promoted) {
        this.promoted = promoted;
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

    public boolean isPadding() {
        return isPadding;
    }

    public void setPadding(boolean padding) {
        isPadding = padding;
    }

    public boolean isFromHolder() {
        return isFromHolder;
    }

    public void setIsHolder(boolean holder) {
        isFromHolder = holder;
    }

    public boolean isFromContributor() {
        return isFromContributor;
    }

    public void setIsContributor(boolean contributor) {
        isFromContributor = contributor;
    }

    public boolean isNiced() {
        return niced;
    }

    public void setNiced(boolean niced) {
        this.niced = niced;
    }

    public boolean isReshooted() {
        return reshooted;
    }

    public void setReshooted(boolean reshooted) {
        this.reshooted = reshooted;
    }

    public Date getNicedTime() {
        return nicedTime;
    }

    public void setNicedTime(Date nicedTime) {
        this.nicedTime = nicedTime;
    }

    public Date getReshootedTime() {
        return reshootedTime;
    }

    public void setReshootedTime(Date reshootedTime) {
        this.reshootedTime = reshootedTime;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }
}