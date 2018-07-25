package com.shootr.mobile.data.entity.socket;

public class UpdateItemSocketMessageEntity extends SocketMessageEntity {

  public UpdateItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.UPDATE_ITEM_DATA);
  }

  private ItemEntity data;

  public ItemEntity getData() {
    return data;
  }

  public void setData(ItemEntity data) {
    this.data = data;
  }
}
