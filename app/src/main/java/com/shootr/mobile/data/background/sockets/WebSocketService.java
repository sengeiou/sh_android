package com.shootr.mobile.data.background.sockets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.entity.MessageMapper;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.CloseSocketEvent;
import com.shootr.mobile.domain.bus.EventReceived;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.timeline.InternalTimelineRepository;
import com.shootr.mobile.notifications.AndroidNotificationManager;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.ui.presenter.WebSocketPresenter;
import com.shootr.mobile.ui.views.SocketView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class WebSocketService extends Service
    implements SocketView, CloseSocketEvent.Receiver {

  private static boolean isRunning = false;
  private static Intent serviceIntent;

  @Inject WebSocketPresenter presenter;

  @Inject BusPublisher busPublisher;
  @Inject @Main Bus bus;
  @Inject SocketMessageWrapper socketMessageWrapper;
  @Inject MessageMapper messageMapper;
  @Inject Gson gson;
  @Inject SessionRepository sessionRepository;
  @Inject AndroidNotificationManager androidNotificationManager;
  @Inject NotificationBuilderFactory notificationBuilderFactory;

  @Inject InternalTimelineRepository localTimelineRepository;

  private int threadCounter;
  private final List<Thread> threadList = new ArrayList<>();

  @Override public void onCreate() {
    super.onCreate();
    ShootrApplication.get(getApplicationContext()).inject(this);
    bus.register(this);
  }

  @Override public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public static void startService(Context context) {
    if (!isRunning) {
      isRunning = true;
      serviceIntent = new Intent(context, WebSocketService.class);
      context.startService(serviceIntent);
    } else {
      Log.d("socket", "intento de abrir servicio que está running");
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    boolean serviceStartedExplicitly = intent != null;
    if (serviceStartedExplicitly) {
      presenter.initialize(this);
    } else {

    }
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.d("socket", "cierro servicio");
    bus.unregister(this);
    isRunning = false;
  }

  @Override public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    isRunning = false;
    stopSelf();
  }

  @Subscribe @Override public void onEvent(CloseSocketEvent.Event event) {
    presenter.closeSocket();
  }

  @Override public void onMessage(SocketMessage socketMessage) {
    busPublisher.post(new EventReceived.Event(socketMessage));
  }

  @Override public void stopService() {
    stopSelf();
  }

}
