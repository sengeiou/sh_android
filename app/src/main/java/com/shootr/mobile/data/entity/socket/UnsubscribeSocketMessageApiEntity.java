package com.shootr.mobile.data.entity.socket;

public class UnsubscribeSocketMessageApiEntity extends SocketMessageApiEntity {

  public UnsubscribeSocketMessageApiEntity() {
    setEventType(SocketMessageApiEntity.UNSUBSCRIBE);
  }

}
