package com.shootr.mobile.data.entity;


public class PinnedItemSocketMessageEntity extends SocketMessageEntity {

  public PinnedItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.PINNED_ITEMS);
  }

  private DataEntity data;

  public DataEntity getData() {
    return data;
  }

  public void setData(DataEntity data) {
    this.data = data;
  }
}
