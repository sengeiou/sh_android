package com.shootr.mobile.domain.model;

import java.util.Date;

public class ExternalVideo implements PrintableItem {

  private String provider;
  private String videoId;
  private Date deleted;
  private String idExternalVideo;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getVideoId() {
    return videoId;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  @Override public String getResultType() {
    return PrintableType.EXTERNAL_VIDEO;
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public Date getDeletedData() {
    return deleted;
  }

  @Override public void setDeletedData(Date deleted) {
    this.deleted = deleted;
  }

  @Override public String getMessageType() {
    return null;
  }

  public String getIdExternalVideo() {
    return idExternalVideo;
  }

  public void setIdExternalVideo(String idExternalVideo) {
    this.idExternalVideo = idExternalVideo;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExternalVideo that = (ExternalVideo) o;

    return getIdExternalVideo() != null ? getIdExternalVideo().equals(that.getIdExternalVideo())
        : that.getIdExternalVideo() == null;
  }

  @Override public int hashCode() {
    return getIdExternalVideo() != null ? getIdExternalVideo().hashCode() : 0;
  }
}
