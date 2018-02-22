package com.shootr.mobile.domain.model;

public class FixedItemSocketMessage extends SocketMessage {

  public FixedItemSocketMessage() {
    setEventType(SocketMessage.FIXED_ITEMS);
  }

  private DataItem data;

  public DataItem getData() {
    return data;
  }

  public void setData(DataItem data) {
    this.data = data;
  }
}
