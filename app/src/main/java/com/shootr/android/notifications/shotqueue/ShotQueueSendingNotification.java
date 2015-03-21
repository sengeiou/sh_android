package com.shootr.android.notifications.shotqueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;

public class ShotQueueSendingNotification extends CommonNotification {

    private final QueuedShot shot;
    private CharSequence titleText;

    public ShotQueueSendingNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_sending);
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(titleText);
        builder.setContentText(shot.getShot().getComment());
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setProgress(100, 0, true);
        builder.setSound(null);
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override public Bitmap getWearBackground() {
        return null;
    }
}