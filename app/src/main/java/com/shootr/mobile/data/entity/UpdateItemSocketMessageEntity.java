package com.shootr.mobile.data.entity;

public class UpdateItemSocketMessageEntity extends SocketMessageEntity {

  public UpdateItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.UPDATE_ITEM_DATA);
  }

  private PrintableItemEntity data;

  public PrintableItemEntity getData() {
    return data;
  }

  public void setData(PrintableItemEntity data) {
    this.data = data;
  }
}
