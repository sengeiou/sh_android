package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class ShotImageModel implements Serializable {

  private String imageUrl;
  private Long imageWidth;
  private Long imageHeight;

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String image) {
    this.imageUrl = image;
  }

  public Long getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(Long imageWidth) {
    this.imageWidth = imageWidth;
  }

  public Long getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(Long imageHeight) {
    this.imageHeight = imageHeight;
  }
}
