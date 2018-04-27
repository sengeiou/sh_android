package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.PrintableType;

public class ShotEntity extends BaseMessageEntity implements PrintableItemEntity {

  private String idShot;

  private String idStream;
  private String streamTitle;
  private String userPhoto;

  private Integer niceCount;

  private String type;

  private String idShotParent;
  private String idUserParent;
  private String userNameParent;

  private Long profileHidden;
  private Long replyCount;

  private Long linkClicks;
  private Long views;

  private Long reshootCounter;

  private Long promoted;
  private String ctaButtonLink;
  private String ctaButtonText;
  private String ctaCaption;
  private Integer isPadding;
  private Integer isFromHolder;
  private Integer isFromContributor;
  private boolean niced;
  private boolean reshooted;
  private Long nicedTime;
  private Long reshootedTime;
  private String shareLink;
  private Long order;

  public String getIdShot() {
    return idShot;
  }

  public void setIdShot(String idShot) {
    this.idShot = idShot;
  }

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getUserPhoto() {
    return userPhoto;
  }

  public void setUserPhoto(String userPhoto) {
    this.userPhoto = userPhoto;
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

  public Long getReshootCounter() {
    return reshootCounter;
  }

  public void setReshootCounter(Long reshootCounter) {
    this.reshootCounter = reshootCounter;
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

  public String getCtaCaption() {
    return ctaCaption;
  }

  public void setCtaCaption(String ctaCaption) {
    this.ctaCaption = ctaCaption;
  }

  public Integer isPadding() {
    return isPadding;
  }

  public void setPadding(Integer padding) {
    isPadding = padding;
  }

  public Integer isFromContributor() {
    return isFromContributor;
  }

  public void setFromContributor(Integer isFromContributor) {
    this.isFromContributor = isFromContributor;
  }

  public Integer isFromHolder() {
    return isFromHolder;
  }

  public void setFromHolder(Integer isFromHolder) {
    this.isFromHolder = isFromHolder;
  }

  public boolean getNiced() {
    return niced;
  }

  public void setNiced(boolean niced) {
    this.niced = niced;
  }

  public boolean getReshooted() {
    return reshooted;
  }

  public void setReshooted(boolean reshooted) {
    this.reshooted = reshooted;
  }

  public Long getNicedTime() {
    return nicedTime;
  }

  public void setNicedTime(Long nicedTime) {
    this.nicedTime = nicedTime;
  }

  public Long getReshootedTime() {
    return reshootedTime;
  }

  public void setReshootedTime(Long reshootedTime) {
    this.reshootedTime = reshootedTime;
  }

  @Override public String getResultType() {
    return PrintableType.SHOT;
  }

  public String getShareLink() {
    return shareLink;
  }

  public void setShareLink(String shareLink) {
    this.shareLink = shareLink;
  }

  public Long getOrder() {
    return order;
  }

  public void setOrder(Long order) {
    this.order = order;
  }
}
