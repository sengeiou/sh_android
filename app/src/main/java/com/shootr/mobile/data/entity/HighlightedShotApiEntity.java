package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.ShotApiEntity;

public class HighlightedShotApiEntity extends Synchronized {

  private String idHighlightedShot;
  private ShotApiEntity shot;
  private Long active;

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

  public ShotApiEntity getShot() {
    return shot;
  }

  public void setShot(ShotApiEntity shot) {
    this.shot = shot;
  }
}