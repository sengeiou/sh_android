package com.shootr.mobile.data.entity.socket;

import com.shootr.mobile.data.entity.EventParams;

public class HighlightSocketMessageApiEntity extends SocketMessageApiEntity {

  public HighlightSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.HIGHLIGHT_SHOT);
  }

  private EventParams data;

  public EventParams getData() {
    return data;
  }

  public void setData(EventParams data) {
    this.data = data;
  }
}
