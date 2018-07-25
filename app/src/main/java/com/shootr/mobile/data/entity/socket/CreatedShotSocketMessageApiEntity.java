package com.shootr.mobile.data.entity.socket;

public class CreatedShotSocketMessageApiEntity extends SocketMessageApiEntity {

  private String idQueue;

  public CreatedShotSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.CREATED_SHOT);
  }

  public String getIdQueue() {
    return idQueue;
  }

  public void setIdQueue(String idQueue) {
    this.idQueue = idQueue;
  }
}
