package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class CardModel implements Serializable {

  private String type;
  private UrlModel link;
  private String title;
  private String duration;
  private ImageMediaModel image;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UrlModel getLink() {
    return link;
  }

  public void setLink(UrlModel link) {
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

  public ImageMediaModel getImage() {
    return image;
  }

  public void setImage(ImageMediaModel image) {
    this.image = image;
  }
}
