package com.shootr.mobile.data.entity.socket;

public class AckSocketMessageApiEntity extends SocketMessageApiEntity {

  public AckSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.ACK);
  }
}
