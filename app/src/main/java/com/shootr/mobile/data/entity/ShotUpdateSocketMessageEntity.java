package com.shootr.mobile.data.entity;

public class ShotUpdateSocketMessageEntity extends SocketMessageEntity {

  public ShotUpdateSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.SHOT_UPDATE);
  }

  private PrintableItemEntity data;

  public PrintableItemEntity getData() {
    return data;
  }

  public void setData(PrintableItemEntity data) {
    this.data = data;
  }
}
