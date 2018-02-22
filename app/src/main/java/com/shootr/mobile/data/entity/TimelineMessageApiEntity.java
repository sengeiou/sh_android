package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.TimelineApiEntity;

public class TimelineMessageApiEntity extends SocketMessageApiEntity {

  public TimelineMessageApiEntity() {
    setEventType(SocketMessageApiEntity.TIMELINE);
  }

  private TimelineApiEntity data;

  public TimelineApiEntity getData() {
    return data;
  }

  public void setData(TimelineApiEntity data) {
    this.data = data;
  }
}
