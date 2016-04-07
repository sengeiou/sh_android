package com.shootr.mobile.notifications.activity;

import android.app.Application;
import android.content.Context;

import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.notifications.AndroidNotificationManager;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ActivityNotificationManager {

    private static final String NOTIFICATION_TAG = "activity";
    private static final int ACTIVITY_NOTIFICATION_ID = 1;

    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final ImageLoader imageLoader;
    private final List<SingleActivityNotification> activeNotifications = new ArrayList<>();
    private final ShotRepository remoteShotRepository;
    private final ShotModelMapper shotModelMapper;

    @Inject
    public ActivityNotificationManager(Application context, AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory, ImageLoader imageLoader,
      @Remote ShotRepository remoteShotRepository, ShotModelMapper shotModelMapper) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.imageLoader = imageLoader;
        this.remoteShotRepository = remoteShotRepository;
        this.shotModelMapper = shotModelMapper;
    }

    private void showNotification(SingleActivityNotification singleActivityNotification) {
        activeNotifications.add(singleActivityNotification);
        CommonNotification finalNotification;
        if (activeNotifications.size() > 1) {
            finalNotification =
              new MultipleActivityNotification(context, imageLoader, notificationBuilderFactory, activeNotifications);
        } else {
            finalNotification = singleActivityNotification;
        }
        androidNotificationManager.notify(finalNotification, NOTIFICATION_TAG, ACTIVITY_NOTIFICATION_ID);
    }

    public void sendGenericActivityNotification(PushNotification.NotificationValues values) {
        SingleActivityNotification notification =
          new SingleActivityNotification(context, notificationBuilderFactory, imageLoader, values);
        showNotification(notification);
    }

    public void sendFollowNotification(PushNotification.NotificationValues values, String idUser) {
        FollowActivityNotification notification =
          new FollowActivityNotification(context, notificationBuilderFactory, imageLoader, values, idUser);
        showNotification(notification);
    }

    public void sendOpenStreamNotification(PushNotification.NotificationValues notificationValues, String idStream,
      String idStreamHolder, String shortTitle) {
        StreamActivityNotification notification =
          new StreamActivityNotification(context, notificationBuilderFactory, imageLoader, notificationValues, idStream, idStreamHolder, shortTitle);
        showNotification(notification);
    }

    public void sendOpenShotDetailNotification(final PushNotification.NotificationValues notificationValues, String idShot) {
        ShotActivityNotification notification = new ShotActivityNotification(context,
          notificationBuilderFactory,
          imageLoader,
          notificationValues,
          idShot,
          remoteShotRepository, shotModelMapper);
        showNotification(notification);
    }

    public void clearActivityNotifications() {
        androidNotificationManager.removeNotification(NOTIFICATION_TAG, ACTIVITY_NOTIFICATION_ID);
        activeNotifications.clear();
    }
}
