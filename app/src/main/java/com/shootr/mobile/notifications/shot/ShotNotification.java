package com.shootr.mobile.notifications.shot;

public class ShotNotification {

  private String idShot;
  private String title;
  private String contentText;
  private String image;
  private String avatarImage;
  private String username;

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

  public String getContentText() {
    return contentText;
  }

  public void setContentText(String contentText) {
    this.contentText = contentText;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getAvatarImage() {
    return avatarImage;
  }

  public void setAvatarImage(String avatarImage) {
    this.avatarImage = avatarImage;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
