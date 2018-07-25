package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.TimelineEntity;

public class TimelineMessageEntity extends SocketMessageEntity {

  public TimelineMessageEntity() {
    setEventType(SocketMessageApiEntity.TIMELINE);
  }

  private TimelineEntity data;

  public TimelineEntity getData() {
    return data;
  }

  public void setData(TimelineEntity data) {
    this.data = data;
  }
}
