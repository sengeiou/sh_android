package com.shootr.mobile.data.entity;

public class SeenParams {

  private String type;
  private String itemId;
  private long timestamp;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
