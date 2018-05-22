package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.ShotDetailApiEntity;

public class ShotDetailSocketMessageApiEntity extends SocketMessageApiEntity {

  private ShotDetailApiEntity data;

  public ShotDetailSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.SHOT_DETAIL);
  }

  public ShotDetailApiEntity getData() {
    return data;
  }

  public void setData(ShotDetailApiEntity data) {
    this.data = data;
  }
}
