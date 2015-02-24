package com.shootr.android.notifications.shotqueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;

public class ShotQueueSendingNotification extends CommonNotification {

    private final QueuedShot shot;

    public ShotQueueSendingNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle("Sending shot...");
        builder.setContentText(shot.getShot().getComment());
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setProgress(100, 0, true);
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override public Bitmap getWearBackground() {
        return null;
    }

    @Override public int getId() {
        return (int) (ShotQueueNotificationManager.NOTIFICATION_ID_PREFIX + (shot.getIdQueue() % 999));
    }
}
