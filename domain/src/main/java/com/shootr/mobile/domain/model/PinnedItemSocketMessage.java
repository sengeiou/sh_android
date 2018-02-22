package com.shootr.mobile.domain.model;

public class PinnedItemSocketMessage extends SocketMessage {

  public PinnedItemSocketMessage() {
    setEventType(SocketMessage.PINNED_ITEMS);
  }

  private DataItem data;

  public DataItem getData() {
    return data;
  }

  public void setData(DataItem data) {
    this.data = data;
  }
}
