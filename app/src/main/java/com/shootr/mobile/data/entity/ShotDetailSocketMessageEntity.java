package com.shootr.mobile.data.entity;

public class ShotDetailSocketMessageEntity extends SocketMessageEntity {

  private NewShotDetailEntity data;

  public ShotDetailSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.SHOT_DETAIL);
  }

  public NewShotDetailEntity getData() {
    return data;
  }

  public void setData(NewShotDetailEntity data) {
    this.data = data;
  }
}
