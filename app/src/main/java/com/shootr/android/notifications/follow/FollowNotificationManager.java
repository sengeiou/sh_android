package com.shootr.android.notifications.follow;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FollowNotificationManager {

    private static final String NOTIFICATION_TAG = "follow";
    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;

    int lastNotificationId = 1;

    @Inject public FollowNotificationManager(Application context, AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
    }

    public void sendNewFollowerNotification(UserModel user) {
        FollowerNotification notification = new FollowerNotification(context, notificationBuilderFactory, user);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, lastNotificationId++);
    }
}
