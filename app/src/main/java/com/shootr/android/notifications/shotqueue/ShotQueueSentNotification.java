package com.shootr.android.notifications.shotqueue;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;

public class ShotQueueSentNotification extends CommonNotification {

    private final QueuedShot shot;
    private CharSequence titleText;

    public ShotQueueSentNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_sent);
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(titleText);
        builder.setContentText(shot.getShot().getComment());
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setSound(null);
        builder.setAutoCancel(true);
        /* Empty content intent for auto-cancel to work */
        builder.setContentIntent(PendingIntent.getActivity(getContext(), 0, new Intent(), 0));
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override public Bitmap getWearBackground() {
        return null;
    }

    @Override
    protected CharSequence getTickerText() {
        return titleText;
    }
}
