package com.shootr.mobile.data.api.entity;

public class CardApiEntity {

  private String type;
  private UrlApiEntity link;
  private String title;
  private String duration;
  private ImageMediaApiEntity image;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UrlApiEntity getLink() {
    return link;
  }

  public void setLink(UrlApiEntity link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public ImageMediaApiEntity getImage() {
    return image;
  }

  public void setImage(ImageMediaApiEntity image) {
    this.image = image;
  }
}
