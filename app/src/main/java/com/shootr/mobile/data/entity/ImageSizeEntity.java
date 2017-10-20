package com.shootr.mobile.data.entity;

public class ImageSizeEntity {

  private SizeEntity low;
  private SizeEntity medium;
  private SizeEntity high;

  public SizeEntity getLow() {
    return low;
  }

  public void setLow(SizeEntity low) {
    this.low = low;
  }

  public SizeEntity getMedium() {
    return medium;
  }

  public void setMedium(SizeEntity medium) {
    this.medium = medium;
  }

  public SizeEntity getHigh() {
    return high;
  }

  public void setHigh(SizeEntity high) {
    this.high = high;
  }

  @Override public String toString() {
    return "ImageSizeEntity{" + "low=" + low + ", medium=" + medium + ", high=" + high + '}';
  }
}