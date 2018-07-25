package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.api.entity.ItemApiEntity;

public class NewItemSocketMessageApiEntity extends SocketMessageApiEntity {

  public NewItemSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.NEW_ITEM_DATA);
  }

  private ItemApiEntity data;

  public ItemApiEntity getData() {
    return data;
  }

  public void setData(ItemApiEntity data) {
    this.data = data;
  }
}
