package com.shootr.mobile.domain.model;

public class ShotUpdateSocketMessage extends SocketMessage {

  public ShotUpdateSocketMessage() {
    setEventType(SocketMessage.SHOT_UPDATE);
  }

  private PrintableItem data;

  public PrintableItem getData() {
    return data;
  }

  public void setData(PrintableItem data) {
    this.data = data;
  }

}

