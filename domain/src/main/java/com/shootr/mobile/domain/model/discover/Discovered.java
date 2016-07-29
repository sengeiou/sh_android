package com.shootr.mobile.domain.model.discover;

import com.shootr.mobile.domain.model.stream.Stream;

public class Discovered {

  private String idDiscover;
  private Long relevance;
  private String type;
  private Stream stream;
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

  public Stream getStream() {
    return stream;
  }

  public void setStream(Stream stream) {
    this.stream = stream;
  }

  public Boolean isFaved() {
    return hasBeenFaved;
  }

  public void setFaved(Boolean faved) {
    hasBeenFaved = faved;
  }
}
