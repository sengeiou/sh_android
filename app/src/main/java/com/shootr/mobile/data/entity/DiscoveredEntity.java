package com.shootr.mobile.data.entity;

public class DiscoveredEntity {

  private String idDiscover;
  private Long relevance;
  private String type;
  private StreamEntity stream;

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
}
