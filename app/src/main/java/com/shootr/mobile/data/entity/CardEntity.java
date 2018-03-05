package com.shootr.mobile.data.entity;

public class CardEntity {

  private String type;
  private UrlEntity link;
  private String title;
  private String duration;
  private ImageMediaEntity image;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UrlEntity getLink() {
    return link;
  }

  public void setLink(UrlEntity link) {
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

  public ImageMediaEntity getImage() {
    return image;
  }

  public void setImage(ImageMediaEntity image) {
    this.image = image;
  }
}
