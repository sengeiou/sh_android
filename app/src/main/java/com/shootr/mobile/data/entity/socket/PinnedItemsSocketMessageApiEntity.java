package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.api.entity.DataApiEntity;

public class PinnedItemsSocketMessageApiEntity extends SocketMessageApiEntity {

  public PinnedItemsSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.PINNED_ITEMS);
  }

  private DataApiEntity data;

  public DataApiEntity getData() {
    return data;
  }

  public void setData(DataApiEntity data) {
    this.data = data;
  }
}
