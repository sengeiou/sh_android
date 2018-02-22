package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;

public class UpdateItemSocketMessageApiEntity extends SocketMessageApiEntity {

  public UpdateItemSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.UPDATE_ITEM_DATA);
  }

  private PrintableItemApiEntity data;

  public PrintableItemApiEntity getData() {
    return data;
  }

  public void setData(PrintableItemApiEntity data) {
    this.data = data;
  }
}
