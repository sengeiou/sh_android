package com.shootr.mobile.data.api;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.shootr.mobile.data.ShotSocketApiManager;
import com.shootr.mobile.data.background.sockets.SocketListener;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class WebSocketApiImpl implements SocketApi, SendSocketEventListener {

  private final String SOCKET_SUBSCRIPTION_ERROR = "SOCKET_SUBSCRIPTION_ERROR: ";
  private final String SOCKET_CONNECTED = "Socket connected succesful";

  private WebSocket webSocket;
  private final int RETRY_TIMES = 50;
  private final int DELAY = 10000;
  private final SocketMessageEntityWrapper socketMessageWrapper;
  private final LogsCache logsCache;
  private HashMap<String, SocketMessageApiEntity> lastEvents;
  private SubscriptionSocketApiManager subscriptionSocketApiService;
  private TimelineSocketApiManager timelineSocketApiManager;
  private ShotSocketApiManager shotSocketApiManager;

  private boolean haveHadSomeError;

  @Inject
  public WebSocketApiImpl(SocketMessageEntityWrapper socketMessageWrapper, LogsCache logsCache) {
    this.socketMessageWrapper = socketMessageWrapper;
    this.logsCache = logsCache;
    lastEvents = new HashMap<>();
    subscriptionSocketApiService = new SubscriptionSocketApiManager(this);
    timelineSocketApiManager = new TimelineSocketApiManager(this);
    shotSocketApiManager = new ShotSocketApiManager(this);
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

  @Override public void sendEvent(SocketMessageApiEntity event) {
    lastEvents.put(event.getRequestId(), event);
    if (webSocket != null && webSocket.isOpen()) {
      webSocket.sendText(socketMessageWrapper.transformEvent(event));
    }
  }

  @Override public void onRestoreLastTimeline(String idStream, String filter,
      PaginationEntity paginationEntity) {
    getTimeline(idStream,
        filter, paginationEntity);
  }

  @Override
  public void updateSocketSubscription(String idStream, String filter, ParamsEntity paramsEntity) {
    subscriptionSocketApiService.updateSocketSubscription(idStream, filter, paramsEntity);
  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter,
      long period) {

    return subscriptionSocketApiService.subscribeToTimeline(subscriptionType, idStream, filter,
        period);
  }

  @Override public boolean subscribeToShotDetail(String subscriptionType, String idShot) {
    return subscriptionSocketApiService.subscribeToShotDetail(subscriptionType, idShot);
  }

  @Override
  public boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity) {

    if (webSocket != null && webSocket.isOpen()) {
      timelineSocketApiManager.getTimeline(idStream, filter, paginationEntity);
      return true;
    }
    return false;
  }

  @Override public boolean getNicestTimeline(String idStream, String filter,
      PaginationEntity paginationEntity, ParamsEntity paramsEntity) {
    if (webSocket != null && webSocket.isOpen()) {
      timelineSocketApiManager.getNicestTimeline(idStream, filter, paginationEntity, paramsEntity);
      return true;
    }

    return false;
  }

  @Override public boolean getShotDetail(String idShot, PaginationEntity promotedPagination,
      PaginationEntity subscribersPagination, PaginationEntity basicPagination) {
    shotSocketApiManager.getShotDetail(idShot, promotedPagination, subscribersPagination,
        basicPagination);
    return true;
  }

  @Override public void unsubscribeShotDetail(String idShot) {
    subscriptionSocketApiService.unsubscribeShotDetail(idShot);
  }

  @Override public boolean sendNice(String idShot) {

    if (webSocket != null && webSocket.isOpen()) {
      shotSocketApiManager.sendNice(idShot);
      return true;
    }

    return false;
  }

  @Override public void closeSocket() {
    if (webSocket != null) {
      webSocket.sendClose();
    }
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
          subscriptionSocketApiService.onSubscriptionAck(event);
        }

        if (event != null && event.getEventType().equals(SocketMessageApiEntity.UNSUBSCRIBE)) {
          subscriptionSocketApiService.onUnsubscriptionAck(event);
        }

        lastEvents.remove(socketMessage.getRequestId());
        break;

      default:
        emitter.onNext(subscriptionSocketApiService.setupSocketMessageEventParams(socketMessage));
        break;
    }
  }

  private void sendLastEvents() {
    if (!lastEvents.isEmpty()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      for (Object o : lastEvents.entrySet()) {
        Map.Entry pair = (Map.Entry) o;
        webSocket.sendText(
            socketMessageWrapper.transformEvent((SocketMessageApiEntity) pair.getValue()));
      }
    }

    subscriptionSocketApiService.subscribeToLastSubscription();
  }


}
