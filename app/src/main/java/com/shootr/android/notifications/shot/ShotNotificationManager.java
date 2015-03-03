package com.shootr.android.notifications.shot;

import android.app.Application;
import android.content.Context;
import com.shootr.android.notifications.AndroidNotificationManager;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotNotificationManager {

    public static final int SHOT_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_TAG = "shot";

    private final Context context;
    private final List<ShotModel> shotsCurrentlyNotified = new ArrayList<>();
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final PicassoWrapper picasso;

    @Inject public ShotNotificationManager(Application context, AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory, PicassoWrapper picasso) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendNewShotNotification(ShotModel shot) {
        shotsCurrentlyNotified.add(shot);

        CommonNotification notification;
        if (shotsCurrentlyNotified.size() > 1) {
            notification = buildMultipleShotNotification(shotsCurrentlyNotified);
        } else {
            notification = buildSingleShotNotification(shot);
        }
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, SHOT_NOTIFICATION_ID);
    }


    protected SingleShotNotification buildSingleShotNotification(ShotModel shot) {
        return new SingleShotNotification(context, notificationBuilderFactory, picasso, shot);
    }

    protected MultipleShotNotification buildMultipleShotNotification(List<ShotModel> shots) {
        return new MultipleShotNotification(context, notificationBuilderFactory, shots);
    }

    public void clearShotNotifications() {
        androidNotificationManager.removeNotification(NOTIFICATION_TAG, SHOT_NOTIFICATION_ID);
        shotsCurrentlyNotified.clear();
    }

}
