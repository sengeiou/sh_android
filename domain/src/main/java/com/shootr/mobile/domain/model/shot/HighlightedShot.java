package com.shootr.mobile.domain.model.shot;

public class HighlightedShot {

  private String idHighlightedShot;
  private Shot shot;
  private Boolean active;
  private Boolean isVisible;

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getIdHighlightedShot() {
    return idHighlightedShot;
  }

  public void setIdHighlightedShot(String idHighlightedShot) {
    this.idHighlightedShot = idHighlightedShot;
  }

  public Shot getShot() {
    return shot;
  }

  public void setShot(Shot shot) {
    this.shot = shot;
  }

  public Boolean isVisible() {
    return isVisible;
  }

  public void setVisible(Boolean visible) {
    isVisible = visible;
  }
}
