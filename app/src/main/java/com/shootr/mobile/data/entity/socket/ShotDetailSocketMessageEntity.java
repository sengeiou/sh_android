package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.ShotDetailEntity;

public class ShotDetailSocketMessageEntity extends SocketMessageEntity {

  private ShotDetailEntity data;

  public ShotDetailSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.SHOT_DETAIL);
  }

  public ShotDetailEntity getData() {
    return data;
  }

  public void setData(ShotDetailEntity data) {
    this.data = data;
  }
}
