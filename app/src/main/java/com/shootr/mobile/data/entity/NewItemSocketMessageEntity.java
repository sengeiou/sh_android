package com.shootr.mobile.data.entity;

public class NewItemSocketMessageEntity extends SocketMessageEntity {

  public NewItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.NEW_ITEM_DATA);
  }

  private PrintableItemEntity data;

  public PrintableItemEntity getData() {
    return data;
  }

  public void setData(PrintableItemEntity data) {
    this.data = data;
  }
}
