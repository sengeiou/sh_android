package com.shootr.mobile.domain.model;


public class NewItemSocketMessage extends SocketMessage {

  public NewItemSocketMessage() {
    setEventType(SocketMessage.NEW_ITEM_DATA);
  }

  private PrintableItem data;

  public PrintableItem getData() {
    return data;
  }

  public void setData(PrintableItem data) {
    this.data = data;
  }

  @Override public String toString() {
    return "NewItemSocketMessage{" + "data=" + data + '}';
  }
}

