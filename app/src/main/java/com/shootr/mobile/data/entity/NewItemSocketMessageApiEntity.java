package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;

public class NewItemSocketMessageApiEntity extends SocketMessageApiEntity {

  public NewItemSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.NEW_ITEM_DATA);
  }

  private PrintableItemApiEntity data;

  public PrintableItemApiEntity getData() {
    return data;
  }

  public void setData(PrintableItemApiEntity data) {
    this.data = data;
  }
}
