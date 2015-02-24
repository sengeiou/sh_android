package com.shootr.android.notifications;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;

import gm.mobi.android.gcm.notifications.StartEventNotification;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShootrNotificationManager {

    public static final int NOTIFICATION_ERROR = 0;
    public static final int NOTIFICATION_SHOT = 1;
    private static final int NOTIFICATION_FOLLOW = 2;
    private static final int NOTIFICATION_START_EVENT = 3;
    private static final int NOTIFICATION_WATCH_REQUEST = 4;

    private Context context;
    private NotificationManagerCompat notificationManager;
    private NotificationBuilderFactory notificationBuilderFactory;
    private PicassoWrapper picasso;

    private List<ShotModel> shotsCurrentlyNotified = new ArrayList<>();

    @Inject public ShootrNotificationManager(Application context, NotificationManagerCompat notificationManager,
                                             NotificationBuilderFactory notificationBuilderFactory, PicassoWrapper picasso) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendNewShotNotification(ShotModel shot) {
        //TODO check if the timeline is currently shown
        shotsCurrentlyNotified.add(shot);

        Notification notification;
        if (shotsCurrentlyNotified.size() > 1) {
            notification = buildMultipleShotNotification(shotsCurrentlyNotified);
        } else {
            notification = buildSingleShotNotification(shot);
        }
        notify(NOTIFICATION_SHOT, notification);
    }

    protected Notification buildSingleShotNotification(ShotModel shot) {
        return new SingleShotNotification(context, notificationBuilderFactory, picasso, shot).build();
    }

    protected Notification buildMultipleShotNotification(List<ShotModel> shots) {
        return new MultipleShotNotification(context, notificationBuilderFactory, shots).build();
    }


    public void sendErrorNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("Error")
          .setContentText(message)
          .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notify(NOTIFICATION_ERROR, builder.build());
    }

    public void clearShotNotifications() {
        notificationManager.cancel(NOTIFICATION_SHOT);
        shotsCurrentlyNotified.clear();
    }

    private void notify(int id, Notification notification) {
        if(areNotificationsEnabled()){
            notificationManager.notify(id, notification);
        }
    }

    public boolean areNotificationsEnabled() {
        return true;
    }

    public void sendNewFollowerNotification(UserModel user) {
        Notification notification = new FollowerNotification(context, notificationBuilderFactory, user).build();
        //TODO ids generados
        notify(NOTIFICATION_FOLLOW, notification);
    }

    public void sendEventStartedNotification(String text) {
        Notification notification = new StartEventNotification(context, notificationBuilderFactory, text).build();
        notify(NOTIFICATION_START_EVENT,notification);
    }

    public void sendWatchRequestNotification(UserWatchingModel userWatchingModel, EventModel eventModel) {
        Notification notification = new WatchRequestNotification(context, notificationBuilderFactory, picasso, userWatchingModel,
          eventModel).build();
        notify(NOTIFICATION_WATCH_REQUEST, notification);
    }
}
