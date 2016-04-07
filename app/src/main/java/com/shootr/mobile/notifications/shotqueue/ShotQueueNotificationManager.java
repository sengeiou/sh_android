package com.shootr.mobile.notifications.shotqueue;

import android.app.Application;
import android.content.Context;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.notifications.AndroidNotificationManager;
import com.shootr.mobile.notifications.NotificationBuilderFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotQueueNotificationManager {

    private static final String NOTIFICATION_TAG = "shotqueue";

    private final Context context;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final AndroidNotificationManager androidNotificationManager;

    @Inject public ShotQueueNotificationManager(Application context,
      NotificationBuilderFactory notificationBuilderFactory, AndroidNotificationManager androidNotificationManager) {
        this.context = context;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.androidNotificationManager = androidNotificationManager;
    }

    public void showSendingShotNotification(QueuedShot shot) {
        ShotQueueSendingNotification notification =
          new ShotQueueSendingNotification(context, notificationBuilderFactory, shot);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }

    public void hideShotNotification(QueuedShot shot) {
        androidNotificationManager.removeNotification(NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }

    public void showShotFailedNotification(QueuedShot shot) {
        ShotQueueFailedNotification notification =
          new ShotQueueFailedNotification(context, notificationBuilderFactory, shot);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }

    public void showShotSentNotification(QueuedShot shot) {
        ShotQueueSentNotification notification =
          new ShotQueueSentNotification(context, notificationBuilderFactory, shot);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }

    public void showShotHasParentDeletedNotification(QueuedShot shot) {
        ShotHasParentDeletedNotification notification =
          new ShotHasParentDeletedNotification(context, notificationBuilderFactory);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }

    public void showShotHasStreamRemovedNotification(QueuedShot shot) {
        ShotHasStreamRemovedNotification notification =
          new ShotHasStreamRemovedNotification(context, notificationBuilderFactory);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, shot.getIdQueue().intValue());
    }
}
