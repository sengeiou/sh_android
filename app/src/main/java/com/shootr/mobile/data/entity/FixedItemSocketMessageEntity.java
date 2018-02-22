package com.shootr.mobile.data.entity;

public class FixedItemSocketMessageEntity extends SocketMessageEntity {

  public FixedItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.FIXED_ITEMS);
  }

  private DataEntity data;

  public DataEntity getData() {
    return data;
  }

  public void setData(DataEntity data) {
    this.data = data;
  }
}
