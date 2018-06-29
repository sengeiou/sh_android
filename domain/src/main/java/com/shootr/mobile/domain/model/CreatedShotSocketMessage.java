package com.shootr.mobile.domain.model;

public class CreatedShotSocketMessage extends SocketMessage {

  private String idQueue;

  public CreatedShotSocketMessage() {
    setEventType(SocketMessage.CREATED_SHOT);
  }

  public String getIdQueue() {
    return idQueue;
  }

  public void setIdQueue(String idQueue) {
    this.idQueue = idQueue;
  }
}
