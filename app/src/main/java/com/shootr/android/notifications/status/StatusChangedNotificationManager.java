package com.shootr.android.notifications.status;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StatusChangedNotificationManager {

    private static final String NOTIFICATION_TAG = "status";
    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final PicassoWrapper picasso;

    @Inject public StatusChangedNotificationManager(Application context,
      AndroidNotificationManager androidNotificationManager, NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendWatchRequestNotification(UserModel userWatchingModel, String newStatus) {
        StatusChangedNotification watchRequestNotification =
          new StatusChangedNotification(context, notificationBuilderFactory, picasso, userWatchingModel, newStatus);
        androidNotificationManager.notify(watchRequestNotification, NOTIFICATION_TAG, userWatchingModel.getIdUser().intValue());
    }
}
