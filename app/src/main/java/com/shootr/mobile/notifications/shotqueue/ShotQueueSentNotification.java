package com.shootr.mobile.notifications.shotqueue;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;

public class ShotQueueSentNotification extends CommonNotification {

    private final QueuedShot shot;
    private CharSequence titleText;

    public ShotQueueSentNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_sent);
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        builder.setContentTitle(titleText);
        builder.setContentText(shot.getBaseMessage().getComment());
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setSound(null);
        builder.setAutoCancel(true);
        /* Empty content intent for auto-cancel to work */
        builder.setContentIntent(PendingIntent.getActivity(getContext(), 0, new Intent(), 0));
        builder.setColor(ContextCompat.getColor(getContext(), R.color.primary_selector));
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    protected CharSequence getTickerText() {
        return titleText;
    }
}
