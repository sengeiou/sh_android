package com.shootr.mobile.data.api.entity;

public class ImagesSizeApiEntity {

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

  public class Sizes {
    String url;
    String width;
    String height;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getWidth() {
      return width;
    }

    public void setWidth(String width) {
      this.width = width;
    }

    public String getHeight() {
      return height;
    }

    public void setHeight(String height) {
      this.height = height;
    }
  }
}
