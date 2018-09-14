package com.shootr.mobile.ui.presenter;

import android.content.Context;
import android.util.Log;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.data.repository.remote.cache.LogsCache;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.CloseSocketInteractor;
import com.shootr.mobile.domain.interactor.ConnectSocketInteractor;
import com.shootr.mobile.domain.interactor.GetSocketInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SubscribePromotedTiersInteractor;
import com.shootr.mobile.domain.interactor.user.DefaultObserver;
import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.ui.views.SocketView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class WebSocketPresenter {

  private final ConnectSocketInteractor connectSocketInteractor;
  private final GetSocketInteractor getSocketInteractor;
  private final CloseSocketInteractor closeSocketInteractor;
  private final SubscribePromotedTiersInteractor subscribePromotedTiersInteractor;
  private final LogsCache logsCache;
  private final Context context;
  private final String SOCKET_CONNECTION = "SOCKET_CONNECTION";
  private final String SOCKET_CONNECTION_ERROR = "SOCKET_CONNECTION_ERROR: ";
  private final String TIMELINE = "TIMELINE";
  private final String NONE = "NONE";
  private final String BOOTSTRAP_RECEIVED = "Bootstrap received. Feature Flags: ";

  private SocketView socketView;
  private final List<Thread> threadList = new ArrayList<>();
  private int threadCounter;

  @Inject public WebSocketPresenter(ConnectSocketInteractor connectSocketInteractor,
      GetSocketInteractor getSocketInteractor, CloseSocketInteractor closeSocketInteractor,
      SubscribePromotedTiersInteractor subscribePromotedTiersInteractor, LogsCache logsCache,
      @ApplicationContext Context context) {
    this.connectSocketInteractor = connectSocketInteractor;
    this.getSocketInteractor = getSocketInteractor;
    this.closeSocketInteractor = closeSocketInteractor;
    this.subscribePromotedTiersInteractor = subscribePromotedTiersInteractor;
    this.logsCache = logsCache;
    this.context = context;
  }

  public void initialize(final SocketView socketView) {
    this.socketView = socketView;
    getSocketInteractor.getSocket(new Interactor.Callback<Bootstrapping>() {
      @Override public void onLoaded(Bootstrapping bootstrapping) {
        if (bootstrapping != null) {
          logsCache.putNewLog(handleLogFeatureFlag(bootstrapping.isSocketConnection()));
          if (bootstrapping.isSocketConnection() && bootstrapping.getSocket() != null) {
            connectSocket(bootstrapping.getSocket().getAddress());
          } else {
            socketView.stopService();
          }
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        logsCache.putNewLog(SOCKET_CONNECTION_ERROR + error.getMessage());
        socketView.stopService();
      }
    });
  }

  private String handleLogFeatureFlag(boolean hasSocket) {
    StringBuilder sb = new StringBuilder();
    sb.append(BOOTSTRAP_RECEIVED);
    sb.append(TIMELINE);
    sb.append(" ");
    if (hasSocket) {
      sb.append(SOCKET_CONNECTION);
    } else {
      sb.append(NONE);
    }
    return sb.toString();
  }

  private void connectSocket(String socketAddess) {
    this.connectSocketInteractor.execute(new ConnectSocketSubscriber(), socketAddess);
  }

  public void closeSocket() {
    closeSocketInteractor.closeSocket(new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        if (socketView != null) {
          socketView.stopService();
        }
      }
    });
  }

  public void subscribeStaticSubscriptions() {
    Log.d("socket", "me suscribo de nuevo a promoted");
    subscribePromotedTiersInteractor.subscribe(new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean aBoolean) {
        /* no-op */
      }
    });
  }

  private final class ConnectSocketSubscriber extends DefaultObserver<SocketMessage> {

    @Override public void onNext(SocketMessage value) {

      String threadName = "message_handler_" + threadCounter++;
      HandleMessageRunnable runnable = new HandleMessageRunnable(value);
      Thread thread = new Thread(runnable, threadName);
      runnable.thread = thread;
      synchronized (threadList) {
        threadList.add(thread);
      }
      thread.start();
    }

    @Override public void onError(Throwable e) {
      Log.d("SOCKETSPRESENTER", "recibo fallo en el presenter" + e.getLocalizedMessage());
      e.printStackTrace();
      socketView.stopService();
    }

    @Override public void onComplete() {
      Log.d("SOCKETSPRESENTER", "he completado");
      socketView.stopService();
    }
  }

  private class HandleMessageRunnable implements Runnable {

    private final SocketMessage message;
    Thread thread;

    private HandleMessageRunnable(SocketMessage message) {
      this.message = message;
    }

    @Override public void run() {

      socketView.onMessage(message);

      synchronized (threadList) {
        threadList.remove(thread);
      }
    }
  }
}
