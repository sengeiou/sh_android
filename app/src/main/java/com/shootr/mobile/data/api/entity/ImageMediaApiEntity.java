package com.shootr.mobile.data.api.entity;

public class ImageMediaApiEntity {

  private String type;
  private ImagesSizeApiEntity sizes;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ImagesSizeApiEntity getSizes() {
    return sizes;
  }

  public void setSizes(ImagesSizeApiEntity sizes) {
    this.sizes = sizes;
  }
}
