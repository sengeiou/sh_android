package com.shootr.mobile.data.api;

import android.util.Log;
import com.shootr.mobile.data.background.sockets.SocketListener;
import com.shootr.mobile.data.entity.EventParams;
import com.shootr.mobile.data.entity.GetTimelineSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NiceSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SubscribeSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UnsubscribeSocketMessageApiEntity;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class SocketApiImpl implements SocketApi {

  private final int VERSION = 1;
  private final int RETRY_TIMES = 50;
  private final int DELAY = 10000;
  private final int MAX_SUBSCRIPTIONS = 2;
  private final SocketMessageEntityWrapper socketMessageWrapper;
  private final OkHttpClient client;
  private WebSocket webSocket;
  private HashMap<String, SocketMessageApiEntity> lastEvents;
  private ArrayList<SubscribeSocketMessageApiEntity> subscriptions;

  @Inject public SocketApiImpl(SocketMessageEntityWrapper socketMessageWrapper, OkHttpClient client) {
    this.socketMessageWrapper = socketMessageWrapper;
    this.client = client;
    lastEvents = new HashMap<>();
    subscriptions = new ArrayList<>();
  }

  @Override public Observable<SocketMessageApiEntity> connect(final String socketAddress) {
    final Request socketRequest = new Request.Builder().url(socketAddress).build();
    return Observable.create(new ObservableOnSubscribe<SocketMessageApiEntity>() {
      @Override public void subscribe(final ObservableEmitter<SocketMessageApiEntity> emitter)
          throws Exception {
        setupSocketConnection(emitter, socketRequest);
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

  private void setupSocketConnection(final ObservableEmitter<SocketMessageApiEntity> emitter,
      Request socketRequest) {
    Log.d("sockets", "esta es la request " + socketRequest);
    webSocket = client.newWebSocket(socketRequest,
        new SocketListener(new SocketListener.WebSocketConnection() {
          @Override public void onFailure(Throwable t) {
            handleOnFailure(t, emitter);
          }

          @Override public void onMessage(String message) {
            SocketMessageApiEntity socketMessage =
                socketMessageWrapper.transformSocketMessage(message);
            if (socketMessage != null) {
              handleNewMessage(socketMessage, emitter);
            }
          }

          @Override public void onConnected() {
            sendLastEvents();
            restoreLastSubscription();
          }

          @Override public void onClosed() {
            emitter.onComplete();
          }
        }));
  }

  private void restoreLastSubscription() {
    if (!subscriptions.isEmpty()) {
      SubscribeSocketMessageApiEntity aux = subscriptions.get(0);
      subscriptions.clear();
      subscribeToTimeline(aux.getData().getSubscriptionType(),
          aux.getData().getIdStream(), aux.getData().getFilter());
      PaginationEntity paginationEntity = new PaginationEntity();
      paginationEntity.setSinceTimestamp(0L);
      getTimeline(aux.getData().getIdStream(), aux.getData().getFilter(), paginationEntity);
    }
  }

  private void handleNewMessage(SocketMessageApiEntity socketMessage,
      ObservableEmitter<SocketMessageApiEntity> emitter) {
    switch (socketMessage.getEventType()) {

      case SocketMessageApiEntity.ACK:

        SocketMessageApiEntity event = lastEvents.get(socketMessage.getRequestId());

        if (event != null && event.getEventType().equals(SocketMessageApiEntity.SUBSCRIBE)) {
          desactiveSubscriptions();
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
              eventParams.setIdStream(subscription.getData().getIdStream());
              socketMessage.setEventParams(eventParams);
            }
          }

        }
        emitter.onNext(socketMessage);
        break;
    }
  }

  private void handleOnFailure(Throwable t, ObservableEmitter<SocketMessageApiEntity> emitter) {
    if (t != null) {
      if (t.getMessage() != null && t.getMessage().equals("closed")) {
        emitter.onComplete();
      }
    } else {
      webSocket.cancel();
      webSocket.close(1000, "bye");
      emitter.onError(t);
    }
  }

  private void sendLastEvents() {
    if (!lastEvents.isEmpty()) {
      Iterator it = lastEvents.entrySet().iterator();

      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry) it.next();
        webSocket.send(
            socketMessageWrapper.transformEvent((SocketMessageApiEntity) pair.getValue()));
      }
    }
  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter) {
    if (webSocket != null) {

      SubscribeSocketMessageApiEntity socketMessageApiEntity =
          new SubscribeSocketMessageApiEntity();

      EventParams data = new EventParams();

      data.setFilter(filter);
      data.setIdStream(idStream);
      data.setSubscriptionType(subscriptionType);

      socketMessageApiEntity.setData(data);

      if (subscriptions.contains(socketMessageApiEntity)) {
        SubscribeSocketMessageApiEntity aux =
            subscriptions.remove(subscriptions.indexOf(socketMessageApiEntity));
        desactiveSubscriptions();
        aux.setActiveSubscription(true);
        subscriptions.add(0, aux);
        return false;
      } else {
        socketMessageApiEntity.setRequestId(generateRequestId());
        socketMessageApiEntity.setVersion(VERSION);
        lastEvents.put(socketMessageApiEntity.getRequestId(), socketMessageApiEntity);
        return webSocket.send(socketMessageWrapper.transformEvent(socketMessageApiEntity));
      }
    }
    return false;
  }

  private void desactiveSubscriptions() {
    for (SubscribeSocketMessageApiEntity subscription : subscriptions) {
      subscription.setActiveSubscription(false);
    }
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
        webSocket.send(socketMessageWrapper.transformEvent(unsubscribeSocketMessageApiEntity));
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
      return webSocket.send(socketMessageWrapper.transformEvent(getTimelineSocketMessageApiEntity));
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
      return webSocket.send(socketMessageWrapper.transformEvent(niceSocketMessageApiEntity));
    }

    return false;
  }

  @Override public void closeSocket() {
    if (webSocket != null) {
      webSocket.close(1000, "bye");
    }
  }

  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }
}
