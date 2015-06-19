package com.shootr.android.notifications.checkin;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CheckinNotificationManager {

    private static final String NOTIFICATION_TAG = "checkin";
    private final Context context;
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final PicassoWrapper picasso;

    int lastNotificationId = 1;

    @Inject
    public CheckinNotificationManager(Application context,
      AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendNewCheckinNotification(ActivityModel activityModel) {
        CheckinNotification notification =
          new CheckinNotification(context, notificationBuilderFactory, picasso, activityModel);
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, lastNotificationId++);
    }
}
