package com.shootr.mobile.ui.model;

public class HighlightedShotModel {

  private String idHighlightedShot;
  private ShotModel shotModel;
  private Boolean isVisible;

  public String getIdHighlightedShot() {
    return idHighlightedShot;
  }

  public void setIdHighlightedShot(String idHighlightedShot) {
    this.idHighlightedShot = idHighlightedShot;
  }

  public ShotModel getShotModel() {
    return shotModel;
  }

  public void setShotModel(ShotModel shotModel) {
    this.shotModel = shotModel;
  }

  public Boolean getVisible() {
    return isVisible;
  }

  public void setVisible(Boolean visible) {
    isVisible = visible;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HighlightedShotModel)) return false;

    HighlightedShotModel that = (HighlightedShotModel) o;

    if (idHighlightedShot != null ? !idHighlightedShot.equals(that.idHighlightedShot)
        : that.idHighlightedShot != null) {
      return false;
    }
    return shotModel != null ? shotModel.equals(that.shotModel) : that.shotModel == null;
  }

  @Override public int hashCode() {
    int result = idHighlightedShot != null ? idHighlightedShot.hashCode() : 0;
    result = 31 * result + (shotModel != null ? shotModel.hashCode() : 0);
    return result;
  }
}
