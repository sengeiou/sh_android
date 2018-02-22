package com.shootr.mobile.domain.model;


public class UpdateItemSocketMessage extends SocketMessage {

  public UpdateItemSocketMessage() {
    setEventType(SocketMessage.UPDATE_ITEM_DATA);
  }

  private PrintableItem data;

  public PrintableItem getData() {
    return data;
  }

  public void setData(PrintableItem data) {
    this.data = data;
  }

}

