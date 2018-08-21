package com.shootr.mobile.domain.model;

public class StreamUpdateSocketMessage extends SocketMessage {

  public StreamUpdateSocketMessage() {
    setEventType(SocketMessage.STREAM_UPDATE);
  }

  private PrintableItem data;

  public PrintableItem getData() {
    return data;
  }

  public void setData(PrintableItem data) {
    this.data = data;
  }

}

