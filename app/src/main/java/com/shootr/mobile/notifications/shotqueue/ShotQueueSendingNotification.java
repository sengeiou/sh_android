package com.shootr.mobile.notifications.shotqueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;

public class ShotQueueSendingNotification extends CommonNotification {

    private final QueuedShot shot;
    private CharSequence titleText;

    public ShotQueueSendingNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_sending);
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        builder.setContentTitle(titleText);
        builder.setContentText(shot.getBaseMessage().getComment());
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setProgress(100, 0, true);
        builder.setSound(null);
    }

    @Override public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    protected CharSequence getTickerText() {
        return titleText;
    }
}
