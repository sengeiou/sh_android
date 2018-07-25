package com.shootr.mobile.data.entity.socket;


public class CreatedShotSocketMessageEntity extends SocketMessageEntity {

  private String idQueue;

  public CreatedShotSocketMessageEntity() {
    setEventType(SocketMessageApiEntity.CREATED_SHOT);
  }

  public String getIdQueue() {
    return idQueue;
  }

  public void setIdQueue(String idQueue) {
    this.idQueue = idQueue;
  }
}
