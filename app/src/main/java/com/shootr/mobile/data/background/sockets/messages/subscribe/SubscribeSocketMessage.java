package com.shootr.mobile.data.background.sockets.messages.subscribe;

import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;

public class SubscribeSocketMessage extends SocketMessageApiEntity {

  private Subscription data;

  public SubscribeSocketMessage() {
    setEventType(SocketMessageApiEntity.SUBSCRIBE);
  }

  public Subscription getData() {
    return data;
  }

  public void setData(Subscription data) {
    this.data = data;
  }
}
