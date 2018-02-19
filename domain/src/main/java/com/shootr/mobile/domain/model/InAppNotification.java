package com.shootr.mobile.domain.model;

import java.io.Serializable;

public class InAppNotification implements Serializable {

  private String idShot;
  private String title;
  private String comment;
  private String avatar;

  public String getIdShot() {
    return idShot;
  }

  public void setIdShot(String idShot) {
    this.idShot = idShot;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  @Override public String toString() {
    return "InAppNotification{"
        + "idShot='"
        + idShot
        + '\''
        + ", title='"
        + title
        + '\''
        + ", comment='"
        + comment
        + '\''
        + ", avatar='"
        + avatar
        + '\''
        + '}';
  }
}
