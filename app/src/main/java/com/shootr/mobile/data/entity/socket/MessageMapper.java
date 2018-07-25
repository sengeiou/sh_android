package com.shootr.mobile.data.entity.socket;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.background.sockets.messages.subscribe.SubscribeSocketMessage;
import com.shootr.mobile.data.background.sockets.messages.subscribe.Subscription;
import com.shootr.mobile.domain.messages.Message;
import com.shootr.mobile.domain.messages.SubscribeMessage;
import javax.inject.Inject;

public class MessageMapper {

  @Inject public MessageMapper() {
  }

  public SocketMessageApiEntity transform(Message message) {

    String messageType = message.getMessageType();

    switch (messageType) {
      case SocketMessageApiEntity.SUBSCRIBE:
        return mapSubscriptionMessage((SubscribeMessage) message);
      default:
        break;
    }
    return null;
  }

  @NonNull private SocketMessageApiEntity mapSubscriptionMessage(SubscribeMessage message) {
    SubscribeSocketMessage subscribeMessage = new SubscribeSocketMessage();

    Subscription subscription = new Subscription();
    subscription.setIdStream(message.getIdStream());
    subscription.setFilter(message.getFilterType());
    subscription.setSubscriptionType(message.getSubscriptionType());

    subscribeMessage.setData(subscription);
    subscribeMessage.setVersion(1234);
    subscribeMessage.setRequestId("requestId");
    return subscribeMessage;
  }
}
