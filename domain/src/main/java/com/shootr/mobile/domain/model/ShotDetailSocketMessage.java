package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.shot.ShotDetail;

public class ShotDetailSocketMessage extends SocketMessage {

  public ShotDetailSocketMessage() {
    setEventType(SocketMessage.SHOT_DETAIL);
  }

  private ShotDetail data;

  public ShotDetail getData() {
    return data;
  }

  public void setData(ShotDetail data) {
    this.data = data;
  }
}
