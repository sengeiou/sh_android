package com.shootr.mobile.data.entity;

public class NewItemSocketMessageEntity extends SocketMessageEntity {

  public NewItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.NEW_ITEM_DATA);
  }

  private ItemEntity data;

  public ItemEntity getData() {
    return data;
  }

  public void setData(ItemEntity data) {
    this.data = data;
  }
}
