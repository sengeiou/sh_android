package com.shootr.mobile.domain.model;


public class PromotedLandingItem {

  private String subtitle;
  private ImageMedia image;
  private PrintableItem data;

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public ImageMedia getImage() {
    return image;
  }

  public void setImage(ImageMedia image) {
    this.image = image;
  }

  public PrintableItem getData() {
    return data;
  }

  public void setData(PrintableItem data) {
    this.data = data;
  }
}
