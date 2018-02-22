package com.shootr.mobile.data.entity;

public class AckSocketMessageApiEntity extends SocketMessageApiEntity {

  public AckSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.ACK);
  }
}
