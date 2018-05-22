package com.shootr.mobile.domain.model;


public class UpdateItemSocketMessage extends SocketMessage {

  public UpdateItemSocketMessage() {
    setEventType(SocketMessage.UPDATE_ITEM_DATA);
  }

  private Item data;

  public Item getData() {
    return data;
  }

  public void setData(Item data) {
    this.data = data;
  }

}

