package com.shootr.mobile.domain.model;

public class ErrorSocketMessage extends SocketMessage {

  private Error data;

  public ErrorSocketMessage() {
    setEventType(SocketMessage.ERROR);
  }

  public Error getData() {
    return data;
  }

  public void setData(Error data) {
    this.data = data;
  }
}
