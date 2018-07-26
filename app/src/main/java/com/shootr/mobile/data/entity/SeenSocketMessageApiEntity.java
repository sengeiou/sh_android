package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class SeenSocketMessageApiEntity extends SocketMessageApiEntity {

  public SeenSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.MARK_ITEM_SEEN);
  }

  private SeenParams data;

  public SeenParams getData() {
    return data;
  }

  public void setData(SeenParams data) {
    this.data = data;
  }
}
