package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.PeriodEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SubscribeSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UnsubscribeSocketMessageApiEntity;
import com.shootr.mobile.domain.model.SubscriptionType;
import java.util.ArrayList;
import java.util.UUID;

public class SubscriptionSocketApiManager {

  private final int MAX_SUBSCRIPTIONS = 2;
  private final int VERSION = 1;

  private ArrayList<SubscribeSocketMessageApiEntity> subscriptions;
  private SendSocketEventListener sendSocketEventListener;

  public SubscriptionSocketApiManager(SendSocketEventListener sendSocketEventListener) {
    this.sendSocketEventListener = sendSocketEventListener;
    this.subscriptions = new ArrayList<>();
  }

  public void onSubscriptionAck(SocketMessageApiEntity event) {
    for (SubscribeSocketMessageApiEntity timelineSubscription : subscriptions) {
      timelineSubscription.setActiveSubscription(false);
    }

    event.setActiveSubscription(true);
    subscriptions.add(0, (SubscribeSocketMessageApiEntity) event);
    if (subscriptions.size() >= MAX_SUBSCRIPTIONS) {
      unsubscribeFirstSubscription();
    }
  }

  public void onUnsubscriptionAck(SocketMessageApiEntity event) {
    for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
      if (subscription.getRequestId().equals(event.getRequestId())) {
        subscriptions.remove(subscription);
      }
    }
  }

  public SocketMessageApiEntity setupSocketMessageEventParams(SocketMessageApiEntity socketMessage) {
    if (!subscriptions.isEmpty()) {
      socketMessage.setActiveSubscription(
          subscriptions.get(0).getRequestId().equals(socketMessage.getRequestId())
              && subscriptions.get(0).isActiveSubscription);

      for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
        EventParams eventParams = new EventParams();
        if (subscription.getRequestId().equals(socketMessage.getRequestId())) {
          eventParams.setFilter(subscription.getData().getFilter());
          eventParams.setParams(subscription.getData().getParams());
          eventParams.setIdShot(subscription.getData().getIdShot());
          eventParams.setIdStream(subscription.getData().getIdStream());
          socketMessage.setEventParams(eventParams);
        }
      }
    }

    return socketMessage;
  }

  public void updateSocketSubscription(String idStream, String filter, ParamsEntity paramsEntity) {
    //TODO pensar cómo podemos hacer este hash más generico
    for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
      int subscriptionHash =
          subscriptionHash(subscription.getData().getIdStream(), subscription.getData().getFilter(),
              subscription.getData().getParams());
      int subscriptionHashForUpdate = subscriptionHash(idStream, filter, paramsEntity);
      if (subscriptionHash == subscriptionHashForUpdate) {
        subscription.getData().setParams(paramsEntity);
        sendSocketEventListener.sendEvent(subscription);
        break;
      }
    }
  }

  public void subscribeToLastSubscription() {
    if (!subscriptions.isEmpty()) {
      SubscribeSocketMessageApiEntity lastSubscription = subscriptions.get(0);
      subscriptions.clear();
      if (lastSubscription.getData().getSubscriptionType().equals(SubscriptionType.TIMELINE)) {
        sendSocketEventListener.onRestoreLastTimeline(lastSubscription.getData().getIdStream(),
            lastSubscription.getData().getFilter(), null);
        /*getTimeline(lastSubscription.getData().getIdStream(),
            lastSubscription.getData().getFilter(), null);*/
        subscribeToTimeline(lastSubscription.getData().getSubscriptionType(),
            lastSubscription.getData().getIdStream(), lastSubscription.getData().getFilter(),
            lastSubscription.getData().getParams() == null ? 0
                : lastSubscription.getData().getParams().getPeriod().getDuration());
      }
    }
  }

  public boolean subscribeToShotDetail(String subscriptionType, String idShot) {

    SubscribeSocketMessageApiEntity socketMessageApiEntity = new SubscribeSocketMessageApiEntity();

    EventParams data = new EventParams();
    data.setSubscriptionType(subscriptionType);
    data.setIdShot(idShot);

    socketMessageApiEntity.setData(data);

    if (subscriptions.contains(socketMessageApiEntity)) {
      SubscribeSocketMessageApiEntity aux =
          subscriptions.remove(subscriptions.indexOf(socketMessageApiEntity));
      aux.setActiveSubscription(true);
      subscriptions.add(0, aux);
      return false;
    } else {
      socketMessageApiEntity.setRequestId(generateRequestId());
      socketMessageApiEntity.setVersion(VERSION);
      sendSocketEventListener.sendEvent(socketMessageApiEntity);
      return true;
    }
  }

  private void unsubscribeFirstSubscription() {
    for (SubscribeSocketMessageApiEntity timelineSubscription : subscriptions) {
      if (subscriptions.indexOf(timelineSubscription) > MAX_SUBSCRIPTIONS) {
        UnsubscribeSocketMessageApiEntity unsubscribeSocketMessageApiEntity =
            new UnsubscribeSocketMessageApiEntity();
        unsubscribeSocketMessageApiEntity.setRequestId(timelineSubscription.getRequestId());
        unsubscribeSocketMessageApiEntity.setVersion(VERSION);
        sendSocketEventListener.sendEvent(unsubscribeSocketMessageApiEntity);
      }
    }
  }

  public void unsubscribeShotDetail(String idShot) {

    for (SubscribeSocketMessageApiEntity timelineSubscription : subscriptions) {
      if (timelineSubscription.getData().getIdShot() != null && timelineSubscription.getData()
          .getIdShot()
          .equals(idShot)) {
        UnsubscribeSocketMessageApiEntity unsubscribeSocketMessageApiEntity =
            new UnsubscribeSocketMessageApiEntity();
        unsubscribeSocketMessageApiEntity.setRequestId(timelineSubscription.getRequestId());
        unsubscribeSocketMessageApiEntity.setVersion(VERSION);
        sendSocketEventListener.sendEvent(unsubscribeSocketMessageApiEntity);
      }
    }
  }

  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter,
      long period) {

    SubscribeSocketMessageApiEntity socketMessageApiEntity = new SubscribeSocketMessageApiEntity();

    EventParams data = new EventParams();

    data.setFilter(filter);
    data.setIdStream(idStream);
    data.setSubscriptionType(subscriptionType);

    if (period != 0) {
      ParamsEntity paramsEntity = new ParamsEntity();
      PeriodEntity periodEntity = new PeriodEntity();
      periodEntity.setDuration(period);

      paramsEntity.setPeriod(periodEntity);

      data.setParams(paramsEntity);
    }

    socketMessageApiEntity.setData(data);

    if (subscriptions.contains(socketMessageApiEntity)) {
      SubscribeSocketMessageApiEntity aux =
          subscriptions.remove(subscriptions.indexOf(socketMessageApiEntity));
      aux.setActiveSubscription(true);
      subscriptions.add(0, aux);
      return false;
    } else {
      socketMessageApiEntity.setRequestId(generateRequestId());
      socketMessageApiEntity.setVersion(VERSION);
      sendSocketEventListener.sendEvent(socketMessageApiEntity);
      return true;
    }
  }

  private int subscriptionHash(String idStream, String filter, ParamsEntity paramsEntity) {
    int result = idStream != null ? idStream.hashCode() : 0;
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (paramsEntity != null ? (int) paramsEntity.getPeriod().getDuration() * 31
        : 0);
    return result;
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }

}
