package com.shootr.mobile.domain.model;


public class PartialUpdateItemSocketMessage extends SocketMessage {

  public PartialUpdateItemSocketMessage() {
    setEventType(SocketMessage.PARTIAL_UPDATE_ITEM_DATA);
  }

  private Item data;

  public Item getData() {
    return data;
  }

  public void setData(Item data) {
    this.data = data;
  }

}

