package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.domain.model.PrintableType;
import java.util.Date;

public class ExternalVideoApiEntity implements PrintableItemApiEntity {

  private String resultType;
  private String provider;
  private String videoId;
  private String idExternalVideo;
  private Date deleted;

  public ExternalVideoApiEntity() {
    setResultType(PrintableType.EXTERNAL_VIDEO);
  }

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

  public String getIdExternalVideo() {
    return idExternalVideo;
  }

  public void setIdExternalVideo(String idExternalVideo) {
    this.idExternalVideo = idExternalVideo;
  }

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override public String getResultType() {
    return resultType;
  }

  @Override public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
