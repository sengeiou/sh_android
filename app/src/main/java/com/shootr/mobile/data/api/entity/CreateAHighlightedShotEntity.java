package com.shootr.mobile.data.api.entity;

/**
 * Parental adversory: Backend guys don't think a shit when doing their endpoints. This
 * Entity is a result of that. The project is becoming a Frankenstein and it's going to be
 * unmaintainable really soon.
 * Arturo - 08/10/2016
 */
public class CreateAHighlightedShotEntity {

  private String idShot;

  public String getIdShot() {
    return idShot;
  }

  public void setIdShot(String idShot) {
    this.idShot = idShot;
  }
}
