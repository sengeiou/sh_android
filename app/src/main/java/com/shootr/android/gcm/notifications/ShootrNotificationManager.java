package com.shootr.android.gcm.notifications;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.picasso.Picasso;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShootrNotificationManager {

    public static final int NOTIFICATION_ERROR = 0;
    public static final int NOTIFICATION_SHOT = 1;
    private static final int NOTIFICATION_FOLLOW = 2;

    private Context context;
    private NotificationManagerCompat notificationManager;
    private NotificationBuilderFactory notificationBuilderFactory;
    private Picasso picasso;

    private List<ShotModel> shotsCurrentlyNotified = new ArrayList<>();

    @Inject public ShootrNotificationManager(Application context, NotificationManagerCompat notificationManager,
                                             NotificationBuilderFactory notificationBuilderFactory, Picasso picasso) {
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
}
