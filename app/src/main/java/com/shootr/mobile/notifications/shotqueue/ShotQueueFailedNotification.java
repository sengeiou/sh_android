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
import com.shootr.mobile.ui.activities.DraftsActivity;

public class ShotQueueFailedNotification extends CommonNotification {

    private final QueuedShot shot;
    private String titleText;
    private String subtitleTextPattern;
    private String subtitleTextPatternWithoutComment;

    public ShotQueueFailedNotification(Context context, NotificationBuilderFactory builderFactory, QueuedShot shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.titleText = context.getResources().getString(R.string.notification_shot_failed);
        this.subtitleTextPattern = context.getResources().getString(R.string.notification_shot_failed_subtitle_pattern);
        this.subtitleTextPatternWithoutComment = context.getResources()
          .getString(R.string.notification_shot_failed_subtitle_pattern_no_comment);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
        builder.setContentTitle(titleText);
        if (shot.getBaseMessage().getComment() != null) {
            builder.setContentText(String.format(subtitleTextPattern, shot.getBaseMessage().getComment()));
        } else {
            builder.setContentText(subtitleTextPatternWithoutComment);
        }
        builder.setSound(null);
        PendingIntent draftsPendingIntent = getOpenIntent();
        builder.setContentIntent(draftsPendingIntent);
        builder.setColor(ContextCompat.getColor(getContext(), R.color.primary_selector));
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
