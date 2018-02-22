package com.shootr.mobile.data.entity;

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
