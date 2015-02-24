package com.shootr.android.notifications.follow;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;

public class FollowNotificationManager {

    public static final int NOTIFICATION_FOLLOW_PREFIX = 1000;
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
        androidNotificationManager.notify(new FollowerNotification(context, notificationBuilderFactory, user));
    }
}
