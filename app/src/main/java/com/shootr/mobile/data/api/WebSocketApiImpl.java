package com.shootr.mobile.data.api;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.shootr.mobile.data.background.sockets.SocketListener;
import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.GetTimelineSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NiceSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.PeriodEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SubscribeSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UnsubscribeSocketMessageApiEntity;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class WebSocketApiImpl implements SocketApi {

  private final String SOCKET_SUBSCRIPTION_ERROR = "SOCKET_SUBSCRIPTION_ERROR: ";
  private final String SOCKET_CONNECTED = "Socket connected succesful";

  private WebSocket webSocket;
  private final int VERSION = 1;
  private final int RETRY_TIMES = 50;
  private final int DELAY = 10000;
  private final int MAX_SUBSCRIPTIONS = 2;
  private final SocketMessageEntityWrapper socketMessageWrapper;
  private final LogsCache logsCache;
  private HashMap<String, SocketMessageApiEntity> lastEvents;
  private ArrayList<SubscribeSocketMessageApiEntity> subscriptions;
  private boolean haveHadSomeError;

  @Inject
  public WebSocketApiImpl(SocketMessageEntityWrapper socketMessageWrapper, LogsCache logsCache) {
    this.socketMessageWrapper = socketMessageWrapper;
    this.logsCache = logsCache;
    lastEvents = new HashMap<>();
    subscriptions = new ArrayList<>();
  }

  @Override public Observable<SocketMessageApiEntity> connect(final String socketAddress) {
    return Observable.create(new ObservableOnSubscribe<SocketMessageApiEntity>() {
      @Override public void subscribe(final ObservableEmitter<SocketMessageApiEntity> emitter)
          throws Exception {
        setupSocketConnection(emitter, socketAddress);
      }
    }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
      @Override public ObservableSource<?> apply(Observable<Throwable> throwableObservable)
          throws Exception {
        return throwableObservable.take(RETRY_TIMES)
            .delay(DELAY, TimeUnit.MILLISECONDS)
            .timeInterval();
      }
    });
  }

  @Override
  public void updateSocketSubscription(String idStream, String filter, ParamsEntity paramsEntity) {
    //TODO pensar cómo podemos hacer este hash más generico
    for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
      int subscriptionHash =
          subscriptionHash(subscription.getData().getIdStream(), subscription.getData().getFilter(),
              subscription.getData().getParams());
      int subscriptionHashForUpdate = subscriptionHash(idStream, filter, paramsEntity);
      if (subscriptionHash == subscriptionHashForUpdate) {
        subscription.getData().setParams(paramsEntity);
        webSocket.sendText(socketMessageWrapper.transformEvent(subscription));
        break;
      }
    }
  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter,
      long period) {
    if (webSocket != null) {

      SubscribeSocketMessageApiEntity socketMessageApiEntity =
          new SubscribeSocketMessageApiEntity();

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
        lastEvents.put(socketMessageApiEntity.getRequestId(), socketMessageApiEntity);
        webSocket.sendText(socketMessageWrapper.transformEvent(socketMessageApiEntity));
        return true;
      }
    }
    return false;
  }

  private void unsubscribeFirstSubscription() {
    for (SubscribeSocketMessageApiEntity timelineSubscription : subscriptions) {
      if (subscriptions.indexOf(timelineSubscription) > MAX_SUBSCRIPTIONS) {
        UnsubscribeSocketMessageApiEntity unsubscribeSocketMessageApiEntity =
            new UnsubscribeSocketMessageApiEntity();
        unsubscribeSocketMessageApiEntity.setRequestId(timelineSubscription.getRequestId());
        unsubscribeSocketMessageApiEntity.setVersion(VERSION);
        lastEvents.put(unsubscribeSocketMessageApiEntity.getRequestId(),
            unsubscribeSocketMessageApiEntity);
        webSocket.sendText(socketMessageWrapper.transformEvent(unsubscribeSocketMessageApiEntity));
      }
    }
  }

  @Override
  public boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity) {

    if (webSocket != null) {
      GetTimelineSocketMessageApiEntity getTimelineSocketMessageApiEntity =
          new GetTimelineSocketMessageApiEntity();
      GetTimelineSocketMessageApiEntity.TimelineParams timelineParams =
          new GetTimelineSocketMessageApiEntity.TimelineParams();

      timelineParams.setFilter(filter);
      timelineParams.setIdStream(idStream);
      timelineParams.setPagination(paginationEntity);

      getTimelineSocketMessageApiEntity.setRequestId(generateRequestId());
      getTimelineSocketMessageApiEntity.setVersion(VERSION);
      getTimelineSocketMessageApiEntity.setData(timelineParams);

      lastEvents.put(getTimelineSocketMessageApiEntity.getRequestId(),
          getTimelineSocketMessageApiEntity);
      webSocket.sendText(socketMessageWrapper.transformEvent(getTimelineSocketMessageApiEntity));
      return true;
    }

    return false;
  }

  @Override public boolean getNicestTimeline(String idStream, String filter,
      PaginationEntity paginationEntity, ParamsEntity paramsEntity) {
    if (webSocket != null) {
      GetTimelineSocketMessageApiEntity getTimelineSocketMessageApiEntity =
          new GetTimelineSocketMessageApiEntity();
      GetTimelineSocketMessageApiEntity.TimelineParams timelineParams =
          new GetTimelineSocketMessageApiEntity.TimelineParams();

      timelineParams.setFilter(filter);
      timelineParams.setIdStream(idStream);
      timelineParams.setPagination(paginationEntity);
      timelineParams.setParams(paramsEntity);

      getTimelineSocketMessageApiEntity.setRequestId(generateRequestId());
      getTimelineSocketMessageApiEntity.setVersion(VERSION);
      getTimelineSocketMessageApiEntity.setData(timelineParams);

      lastEvents.put(getTimelineSocketMessageApiEntity.getRequestId(),
          getTimelineSocketMessageApiEntity);
      webSocket.sendText(socketMessageWrapper.transformEvent(getTimelineSocketMessageApiEntity));
      return true;
    }

    return false;
  }

  @Override public boolean sendNice(String idShot) {

    if (webSocket != null) {
      NiceSocketMessageApiEntity niceSocketMessageApiEntity = new NiceSocketMessageApiEntity();
      EventParams eventParams = new EventParams();

      eventParams.setIdShot(idShot);

      niceSocketMessageApiEntity.setRequestId(generateRequestId());
      niceSocketMessageApiEntity.setVersion(VERSION);
      niceSocketMessageApiEntity.setData(eventParams);

      lastEvents.put(niceSocketMessageApiEntity.getRequestId(), niceSocketMessageApiEntity);
      webSocket.sendText(socketMessageWrapper.transformEvent(niceSocketMessageApiEntity));
      return true;
    }

    return false;
  }

  @Override public void closeSocket() {
    if (webSocket != null) {
      webSocket.sendClose();
    }
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  private void setupSocketConnection(final ObservableEmitter<SocketMessageApiEntity> emitter,
      String address) throws Exception {
    try {
      webSocket = new WebSocketFactory().setVerifyHostname(false)
          .setConnectionTimeout(10000)
          .createSocket(address)
          .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
          .addListener(new WebSocketListenerImpl(new SocketListener.WebSocketConnection() {
            @Override public void onFailure(Throwable t) {
              handleOnFailure(t, emitter);
            }

            @Override public void onMessage(String message) {
              SocketMessageApiEntity socketMessage = socketMessageWrapper.transformSocketMessage(message);
              if (socketMessage != null) {
                handleNewMessage(socketMessage, emitter);
              }
            }

            @Override public void onConnected() {
              haveHadSomeError = false;
              sendLastEvents();
              logsCache.putNewLog(SOCKET_CONNECTED);
            }

            @Override public void onClosed() {
              if (!haveHadSomeError) {
                emitter.onComplete();
              }
            }
          }))
          .connect()
          .setPingInterval(10000);
    } catch (OpeningHandshakeException error) {
      logsCache.putNewLog(SOCKET_SUBSCRIPTION_ERROR + error.getMessage());
      emitter.onComplete();
    } catch (WebSocketException exception) {
      logsCache.putNewLog(SOCKET_SUBSCRIPTION_ERROR + exception.getMessage());
      emitter.onComplete();
    }
  }

  private void handleOnFailure(Throwable t, ObservableEmitter<SocketMessageApiEntity> emitter) {
    haveHadSomeError = true;
    if (t != null) {
      if (t.getMessage() != null) {
        logsCache.putNewLog(SOCKET_SUBSCRIPTION_ERROR + t.getMessage());
        webSocket.sendClose();
        if (!emitter.isDisposed()) {
          emitter.onError(new Throwable(t.getMessage()));
        }
      }
    }
  }

  private void handleNewMessage(SocketMessageApiEntity socketMessage,
      ObservableEmitter<SocketMessageApiEntity> emitter) {
    switch (socketMessage.getEventType()) {

      case SocketMessageApiEntity.ACK:

        SocketMessageApiEntity event = lastEvents.get(socketMessage.getRequestId());

        if (event != null && event.getEventType().equals(SocketMessageApiEntity.SUBSCRIBE)) {
          for (SubscribeSocketMessageApiEntity timelineSubscription : subscriptions) {
            timelineSubscription.setActiveSubscription(false);
          }

          ((SubscribeSocketMessageApiEntity) event).setActiveSubscription(true);
          subscriptions.add(0, (SubscribeSocketMessageApiEntity) event);
          if (subscriptions.size() >= MAX_SUBSCRIPTIONS) {
            unsubscribeFirstSubscription();
          }
        }

        if (event != null && event.getEventType().equals(SocketMessageApiEntity.UNSUBSCRIBE)) {
          for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
            if (subscription.getRequestId().equals(event.getRequestId())) {
              subscriptions.remove(subscription);
            }
          }
        }

        lastEvents.remove(socketMessage.getRequestId());
        break;

      default:
        if (!subscriptions.isEmpty()) {
          socketMessage.setActiveSubscription(
              subscriptions.get(0).getRequestId().equals(socketMessage.getRequestId())
                  && subscriptions.get(0).isActiveSubscription);

          for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
            EventParams eventParams = new EventParams();
            if (subscription.getRequestId().equals(socketMessage.getRequestId())) {
              eventParams.setFilter(subscription.getData().getFilter());
              eventParams.setParams(subscription.getData().getParams());
              socketMessage.setEventParams(eventParams);
            }
          }
        }
        emitter.onNext(socketMessage);
        break;
    }
  }

  private void sendLastEvents() {
    if (!lastEvents.isEmpty()) {
      Iterator it = lastEvents.entrySet().iterator();

      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry) it.next();
        webSocket.sendText(
            socketMessageWrapper.transformEvent((SocketMessageApiEntity) pair.getValue()));
      }
    }

    subscribeToLastSubscription();
  }

  private void subscribeToLastSubscription() {
    if (!subscriptions.isEmpty()) {
      SubscribeSocketMessageApiEntity lastSubscription = subscriptions.get(0);
      subscriptions.clear();
      getTimeline(lastSubscription.getData().getIdStream(), lastSubscription.getData().getFilter(),
          null);
      subscribeToTimeline(lastSubscription.getData().getSubscriptionType(),
          lastSubscription.getData().getIdStream(), lastSubscription.getData().getFilter(),
          lastSubscription.getData().getParams() == null ? 0
              : lastSubscription.getData().getParams().getPeriod().getDuration());
    }
  }

  private int subscriptionHash(String idStream, String filter, ParamsEntity paramsEntity) {
    int result = idStream != null ? idStream.hashCode() : 0;
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (paramsEntity != null ? (int) paramsEntity.getPeriod().getDuration() * 31
        : 0);
    return result;
  }
}
