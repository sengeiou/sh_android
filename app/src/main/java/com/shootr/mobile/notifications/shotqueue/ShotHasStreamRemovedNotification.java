package com.shootr.mobile.notifications.shotqueue;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.ui.activities.DraftsActivity;

public class ShotHasStreamRemovedNotification extends CommonNotification {

    private String titleText;
    private String subtitleText;

    public ShotHasStreamRemovedNotification(Context context, NotificationBuilderFactory builderFactory) {
        super(context, builderFactory);
        this.titleText = context.getResources().getString(R.string.notification_shot_failed);
        this.subtitleText = context.getResources().getString(R.string.notification_shot_has_stream_removed_subtitle);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
        builder.setContentTitle(titleText);
        builder.setContentText(titleText);
        builder.setContentText(subtitleText);
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
