package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.StreamEntity;

public class DiscoveredApiEntity {

  private String idDiscover;
  private Long relevance;
  private String type;
  private StreamEntity stream;
  private ShotApiEntity shot;

  public String getIdDiscover() {
    return idDiscover;
  }

  public void setIdDiscover(String idDiscover) {
    this.idDiscover = idDiscover;
  }

  public Long getRelevance() {
    return relevance;
  }

  public void setRelevance(Long relevance) {
    this.relevance = relevance;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public ShotApiEntity getShot() {
    return shot;
  }

  public void setShot(ShotApiEntity shot) {
    this.shot = shot;
  }
}
