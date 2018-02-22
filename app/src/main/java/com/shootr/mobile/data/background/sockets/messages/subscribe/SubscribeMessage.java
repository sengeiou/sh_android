package com.shootr.mobile.data.background.sockets.messages.subscribe;

import com.shootr.mobile.domain.model.SocketMessage;

public class SubscribeMessage extends SocketMessage {

  private Subscription data;

  public Subscription getData() {
    return data;
  }

  public void setData(Subscription data) {
    this.data = data;
  }
}
