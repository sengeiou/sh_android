package com.shootr.mobile.data.entity;

public class ImageMediaEntity {

  private String type;
  private ImageSizeEntity sizes;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ImageSizeEntity getSizes() {
    return sizes;
  }

  public void setSizes(ImageSizeEntity sizes) {
    this.sizes = sizes;
  }

  @Override public String toString() {
    return "ImageMediaEntity{" + "type='" + type + '\'' + ", sizes=" + sizes + '}';
  }
}
