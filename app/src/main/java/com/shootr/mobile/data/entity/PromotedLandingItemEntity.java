package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;

public class PromotedLandingItemEntity {

  private String subtitle;
  private ImageMediaEntity image;
  private PrintableItemApiEntity data;

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public ImageMediaEntity getImage() {
    return image;
  }

  public void setImage(ImageMediaEntity image) {
    this.image = image;
  }

  public PrintableItemApiEntity getData() {
    return data;
  }

  public void setData(PrintableItemApiEntity data) {
    this.data = data;
  }
}
