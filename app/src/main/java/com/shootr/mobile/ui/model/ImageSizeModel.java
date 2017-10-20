package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class ImageSizeModel implements Serializable {

  private SizesModel low;
  private SizesModel medium;
  private SizesModel high;

  public SizesModel getLow() {
    return low;
  }

  public void setLow(SizesModel low) {
    this.low = low;
  }

  public SizesModel getMedium() {
    return medium;
  }

  public void setMedium(SizesModel medium) {
    this.medium = medium;
  }

  public SizesModel getHigh() {
    return high;
  }

  public void setHigh(SizesModel high) {
    this.high = high;
  }
}
