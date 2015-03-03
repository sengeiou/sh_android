package com.shootr.android.notifications.follow;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;

public class FollowNotificationManager {

    private static final String NOTIFICATION_TAG = "follow";
    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final PicassoWrapper picasso;

    @Inject public FollowNotificationManager(Application context, AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory, PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendNewFollowerNotification(UserModel user) {
        FollowerNotification notification = new FollowerNotification(context, notificationBuilderFactory, user);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, user.getIdUser().intValue());
    }
}
