package com.shootr.mobile.domain.model;

public class ImageSize {

  private Sizes low;
  private Sizes medium;
  private Sizes high;

  public Sizes getLow() {
    return low;
  }

  public void setLow(Sizes low) {
    this.low = low;
  }

  public Sizes getMedium() {
    return medium;
  }

  public void setMedium(Sizes medium) {
    this.medium = medium;
  }

  public Sizes getHigh() {
    return high;
  }

  public void setHigh(Sizes high) {
    this.high = high;
  }
}
