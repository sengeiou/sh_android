package com.shootr.mobile.data.entity;

public class NewShotParameters {

  private String comment;
  private String idStream;
  private String type;
  private String imageIdMedia;
  private String idShotParent;
  private String idQueue;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getImageMedia() {
    return imageIdMedia;
  }

  public void setImageMedia(String imageMedia) {
    this.imageIdMedia = imageMedia;
  }

  public String getIdShotParent() {
    return idShotParent;
  }

  public void setIdShotParent(String idShotParent) {
    this.idShotParent = idShotParent;
  }

  public String getIdQueue() {
    return idQueue;
  }

  public void setIdQueue(String idQueue) {
    this.idQueue = idQueue;
  }
}
