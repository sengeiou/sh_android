package com.shootr.android.notifications.shotqueue;

import android.app.Application;
import android.content.Context;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import javax.inject.Inject;

public class ShotQueueNotificationManager {

    public static final int NOTIFICATION_ID_PREFIX = 3000;

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
        androidNotificationManager.notify(notification);
    }

    public void hideSendingShotNotification(QueuedShot shot) {
        androidNotificationManager.removeNotification((int) (NOTIFICATION_ID_PREFIX + (shot.getIdQueue() % 999)));
    }
}
