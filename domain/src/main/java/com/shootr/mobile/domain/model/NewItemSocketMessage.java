package com.shootr.mobile.domain.model;


public class NewItemSocketMessage extends SocketMessage {

  public NewItemSocketMessage() {
    setEventType(SocketMessage.NEW_ITEM_DATA);
  }

  private Item data;

  public Item getData() {
    return data;
  }

  public void setData(Item data) {
    this.data = data;
  }

  @Override public String toString() {
    return "NewItemSocketMessage{" + "data=" + data + '}';
  }
}

