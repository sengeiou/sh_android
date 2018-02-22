package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.DataApiEntity;

public class FixedItemsSocketMessagesApiEntity extends SocketMessageApiEntity {

  public FixedItemsSocketMessagesApiEntity() {
    setEventType(SocketMessageApiEntity.FIXED_ITEMS);
  }

  private DataApiEntity data;

  public DataApiEntity getData() {
    return data;
  }

  public void setData(DataApiEntity data) {
    this.data = data;
  }
}
