package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.ItemEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;

public class PartialUpdateItemSocketMessageEntity extends SocketMessageEntity {

  public PartialUpdateItemSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.PARTIAL_UPDATE_ITEM_DATA);
  }

  private ItemEntity data;

  public ItemEntity getData() {
    return data;
  }

  public void setData(ItemEntity data) {
    this.data = data;
  }
}
