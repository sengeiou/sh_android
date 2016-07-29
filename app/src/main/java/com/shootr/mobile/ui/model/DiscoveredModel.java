package com.shootr.mobile.ui.model;

public class DiscoveredModel {

  private String idDiscover;
  private Long relevance;
  private String type;
  private StreamModel streamModel;
  private Boolean hasBeenFaved;

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

  public StreamModel getStreamModel() {
    return streamModel;
  }

  public void setStreamModel(StreamModel streamModel) {
    this.streamModel = streamModel;
  }

  public Boolean getHasBeenFaved() {
    return hasBeenFaved;
  }

  public void setHasBeenFaved(Boolean hasBeenFaved) {
    this.hasBeenFaved = hasBeenFaved;
  }
}
