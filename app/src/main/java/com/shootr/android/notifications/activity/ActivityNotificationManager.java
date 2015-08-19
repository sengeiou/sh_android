package com.shootr.android.notifications.activity;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.activity.checkin.CheckinNotification;
import com.shootr.android.notifications.activity.nice.NicedShotNotification;
import com.shootr.android.notifications.activity.startedshooting.StartedShootingNotification;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;
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
    private final PicassoWrapper picasso;
    private final List<SingleActivityNotification> activeNotifications = new ArrayList<>();

    @Inject
    public ActivityNotificationManager(Application context,
      AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    private void showNotification(SingleActivityNotification singleActivityNotification) {
        activeNotifications.add(singleActivityNotification);
        CommonNotification finalNotification;
        if (activeNotifications.size() > 1) {
            finalNotification = new MultipleActivityNotification(context, notificationBuilderFactory, activeNotifications);
        } else {
            finalNotification = singleActivityNotification;
        }
        androidNotificationManager.notify(finalNotification, NOTIFICATION_TAG, ACTIVITY_NOTIFICATION_ID);
    }

    public void sendNewCheckinNotification(ActivityModel activityModel) {
        CheckinNotification notification =
          new CheckinNotification(context, notificationBuilderFactory, picasso, activityModel);
        showNotification(notification);
    }

    public void sendNewStartedShootingNotification(ActivityModel activityModel) {
        StartedShootingNotification notification =
          new StartedShootingNotification(context, notificationBuilderFactory, picasso, activityModel);
        showNotification(notification);
    }

    public void sendNewNicedShotNotification(ActivityModel activityModel) {
        NicedShotNotification notification =
          new NicedShotNotification(context, notificationBuilderFactory, picasso, activityModel);
        showNotification(notification);
    }

    public void clearActivityNotifications() {
        androidNotificationManager.removeNotification(NOTIFICATION_TAG, ACTIVITY_NOTIFICATION_ID);
        activeNotifications.clear();
    }
}