package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.ImageMedia;

public class Card {

  private String type;
  private Url link;
  private String title;
  private String duration;
  private ImageMedia image;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Url getLink() {
    return link;
  }

  public void setLink(Url link) {
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

  public ImageMedia getImage() {
    return image;
  }

  public void setImage(ImageMedia image) {
    this.image = image;
  }
}
