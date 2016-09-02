package com.shootr.mobile.ui.model;

public class DiscoveredModel {

  private String idDiscover;
  private Long relevance;
  private String type;
  private StreamModel streamModel;
  private ShotModel shotModel;
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

  public ShotModel getShotModel() {
    return shotModel;
  }

  public void setShotModel(ShotModel shotModel) {
    this.shotModel = shotModel;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DiscoveredModel that = (DiscoveredModel) o;

    if (idDiscover != null ? !idDiscover.equals(that.idDiscover) : that.idDiscover != null) {
      return false;
    }
    if (relevance != null ? !relevance.equals(that.relevance) : that.relevance != null)
      return false;
    if (type != null ? !type.equals(that.type) : that.type != null) return false;
    if (streamModel != null ? !streamModel.equals(that.streamModel) : that.streamModel != null) {
      return false;
    }
    return hasBeenFaved != null ? hasBeenFaved.equals(that.hasBeenFaved)
        : that.hasBeenFaved == null;
  }

  @Override public int hashCode() {
    int result = idDiscover != null ? idDiscover.hashCode() : 0;
    result = 31 * result + (relevance != null ? relevance.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (streamModel != null ? streamModel.hashCode() : 0);
    result = 31 * result + (hasBeenFaved != null ? hasBeenFaved.hashCode() : 0);
    return result;
  }
}
