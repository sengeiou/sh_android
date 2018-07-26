package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.ItemApiEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class PartialUpdateItemSocketMessageApiEntity extends SocketMessageApiEntity {

  public PartialUpdateItemSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.PARTIAL_UPDATE_ITEM_DATA);
  }

  private ItemApiEntity data;

  public ItemApiEntity getData() {
    return data;
  }

  public void setData(ItemApiEntity data) {
    this.data = data;
  }
}
