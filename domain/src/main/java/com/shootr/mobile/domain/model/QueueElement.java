package com.shootr.mobile.domain.model;

public class QueueElement {

  private String id;
  private String type;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override public String toString() {
    return "QueueElement{" + "id=" + id + ", type=" + type + '}';
  }
}
