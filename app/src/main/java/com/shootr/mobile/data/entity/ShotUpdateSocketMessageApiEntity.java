package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;

public class ShotUpdateSocketMessageApiEntity extends SocketMessageApiEntity {

  public ShotUpdateSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.SHOT_UPDATE);
  }

  private PrintableItemApiEntity data;

  public PrintableItemApiEntity getData() {
    return data;
  }

  public void setData(PrintableItemApiEntity data) {
    this.data = data;
  }
}
