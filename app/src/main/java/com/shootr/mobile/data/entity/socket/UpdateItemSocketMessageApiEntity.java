package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.api.entity.ItemApiEntity;

public class UpdateItemSocketMessageApiEntity extends SocketMessageApiEntity {

  public UpdateItemSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.UPDATE_ITEM_DATA);
  }

  private ItemApiEntity data;

  public ItemApiEntity getData() {
    return data;
  }

  public void setData(ItemApiEntity data) {
    this.data = data;
  }
}
