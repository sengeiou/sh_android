package com.shootr.android.notifications.watch;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;

public class WatchRequestNotificationManager {

    public static final int NOTIFICATION_WATCH_REQUEST_PREFIX = 2000;
    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final PicassoWrapper picasso;

    @Inject public WatchRequestNotificationManager(Application context,
      AndroidNotificationManager androidNotificationManager, NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendWatchRequestNotification(UserWatchingModel userWatchingModel, EventModel eventModel) {
        WatchRequestNotification watchRequestNotification =
          new WatchRequestNotification(context, notificationBuilderFactory, picasso, userWatchingModel, eventModel);
        androidNotificationManager.notify(watchRequestNotification);
    }
}
