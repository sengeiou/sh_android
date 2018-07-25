package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.EventParams;

public class NiceSocketMessageApiEntity extends SocketMessageApiEntity {

  public NiceSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.NICE_SHOT);
  }

  private EventParams data;

  public EventParams getData() {
    return data;
  }

  public void setData(EventParams data) {
    this.data = data;
  }
}
