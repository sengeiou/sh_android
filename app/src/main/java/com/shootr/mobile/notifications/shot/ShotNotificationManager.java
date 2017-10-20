package com.shootr.mobile.notifications.shot;

import android.app.Application;
import android.content.Context;
import com.shootr.mobile.notifications.AndroidNotificationManager;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.util.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotNotificationManager {

    public static final int SHOT_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_TAG = "shot";

    private final Context context;
    private final HashMap<String, ShotNotification> shotsCurrentlyNotified = new HashMap<>();
    private final AndroidNotificationManager androidNotificationManager;
    private final NotificationBuilderFactory notificationBuilderFactory;
    private final ImageLoader imageLoader;

    @Inject public ShotNotificationManager(Application context, AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory, ImageLoader imageLoader) {
        this.context = context;
        this.androidNotificationManager = androidNotificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.imageLoader = imageLoader;
    }

    public void sendNewShotNotification(ShotNotification shot, Boolean areShotTypesKnown) {
        shotsCurrentlyNotified.put(shot.getIdShot(), shot);

        CommonNotification notification;
        if (shotsCurrentlyNotified.size() > 1) {
            notification = buildMultipleShotNotification(new ArrayList<>(shotsCurrentlyNotified.values()));
        } else {
            notification = buildSingleShotNotification(shot, areShotTypesKnown);
        }
        androidNotificationManager.notify(notification, NOTIFICATION_TAG, SHOT_NOTIFICATION_ID);
    }


    protected SingleShotNotification buildSingleShotNotification(ShotNotification shot,
        Boolean areShotTypesKnown) {
        return new SingleShotNotification(context, notificationBuilderFactory, imageLoader, shot, areShotTypesKnown);
    }

    protected MultipleShotNotification buildMultipleShotNotification(List<ShotNotification> shots) {
        return new MultipleShotNotification(context, notificationBuilderFactory, shots);
    }

    public void clearShotNotifications() {
        androidNotificationManager.removeNotification(NOTIFICATION_TAG, SHOT_NOTIFICATION_ID);
        shotsCurrentlyNotified.clear();
    }
}
