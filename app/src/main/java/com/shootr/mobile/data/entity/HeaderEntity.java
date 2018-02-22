package com.shootr.mobile.data.entity;

public class HeaderEntity {

  public static final String SHOT_TYPE = "SHOT";
  public static final String TOPIC_TYPE = "TOPIC";

  private String resultType;
  private String idShot;
  private String comment;

  public String getType() {
    return resultType;
  }

  public void setType(String type) {
    this.resultType = type;
  }

  public String getIdShot() {
    return idShot;
  }

  public void setIdShot(String idShot) {
    this.idShot = idShot;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
