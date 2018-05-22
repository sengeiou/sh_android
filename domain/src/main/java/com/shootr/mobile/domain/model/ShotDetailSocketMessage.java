package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.shot.NewShotDetail;

public class ShotDetailSocketMessage extends SocketMessage {

  public ShotDetailSocketMessage() {
    setEventType(SocketMessage.SHOT_DETAIL);
  }

  private NewShotDetail data;

  public NewShotDetail getData() {
    return data;
  }

  public void setData(NewShotDetail data) {
    this.data = data;
  }
}
