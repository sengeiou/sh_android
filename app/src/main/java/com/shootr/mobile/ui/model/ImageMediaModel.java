package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class ImageMediaModel implements Serializable {

  private String type;
  private ImageSizeModel sizes;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ImageSizeModel getSizes() {
    return sizes;
  }

  public void setSizes(ImageSizeModel sizes) {
    this.sizes = sizes;
  }
}
