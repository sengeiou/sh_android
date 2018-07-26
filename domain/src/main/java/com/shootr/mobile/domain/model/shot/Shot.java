package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.messages.Message;
import com.shootr.mobile.domain.model.Seenable;
import java.util.Comparator;
import java.util.Date;

public class Shot extends BaseMessage implements Seenable {

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
  private Boolean niced;
  private Boolean reshooted;
  private Date nicedTime;
  private Date reshootedTime;
  private String shareLink;
  private Long order;
  private PromotedShotParams promotedShotParams;
  private Boolean seen;

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
    if (o == null || getClass() != o.getClass()) return false;

    Shot shot = (Shot) o;

    return getIdShot() != null ? getIdShot().equals(shot.getIdShot()) : shot.getIdShot() == null;
  }

  @Override public int hashCode() {
    return getIdShot() != null ? getIdShot().hashCode() : 0;
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

  public void setOrder(Long order) {
    this.order = order;
  }

  @Override public Long getOrder() {
    return order;
  }

  @Override public Date getDeletedData() {
    return getMetadata().getDeleted();
  }

  @Override public void setDeletedData(Date deleted) {
    getMetadata().setDeleted(deleted);
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
      return "ShotStreamInfo{"
          + "streamTitle='"
          + streamTitle
          + '\''
          + ", idStream="
          + idStream
          + '}';
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

  public Boolean getNiced() {
    return niced;
  }

  public void setNiced(boolean niced) {
    this.niced = niced;
  }

  public Boolean getReshooted() {
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

  public PromotedShotParams getPromotedShotParams() {
    return promotedShotParams;
  }

  public void setPromotedShotParams(PromotedShotParams promotedShotParams) {
    this.promotedShotParams = promotedShotParams;
  }

  @Override public Boolean getSeen() {
    return seen;
  }

  @Override public void setSeen(Boolean seen) {
    this.seen = seen;
  }

  public static class PromotedShotParams {
    private String type;
    private String data;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }
  }
}