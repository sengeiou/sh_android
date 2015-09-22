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
import com.shootr.android.ui.activities.DraftsActivity;

public class ShotQueueFailedNotification extends CommonNotification {

    private final QueuedShot shot;
    private String titleText;
    private String subtitleTextPattern;

    public ShotQueueFailedNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_failed);
        this.subtitleTextPattern = context.getResources().getString(R.string.notification_shot_failed_subtitle_pattern);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(titleText);
        builder.setContentText(String.format(subtitleTextPattern, shot.getShot().getComment()));
        builder.setSound(null);
        PendingIntent draftsPendingIntent = getOpenIntent();
        builder.setContentIntent(draftsPendingIntent);
    }

    protected PendingIntent getOpenIntent() {
        Intent draftsIntent = new Intent(getContext(), DraftsActivity.class);
        return PendingIntent.getActivity(getContext(), 0, draftsIntent, 0);
    }

    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    protected CharSequence getTickerText() {
        return titleText;
    }
}
