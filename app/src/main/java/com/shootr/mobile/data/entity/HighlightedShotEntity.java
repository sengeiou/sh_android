package com.shootr.mobile.data.entity;

public class HighlightedShotEntity extends ShotEntity {

  private String idHighlightedShot;
  private Long active;
  private Boolean isVisible;

  public Long getActive() {
    return active;
  }

  public void setActive(Long active) {
    this.active = active;
  }

  public String getIdHighlightedShot() {
    return idHighlightedShot;
  }

  public void setIdHighlightedShot(String idHighlightedShot) {
    this.idHighlightedShot = idHighlightedShot;
  }

  public Boolean isVisible() {
    return isVisible;
  }

  public void setVisible(Boolean visible) {
    isVisible = visible;
  }
}
