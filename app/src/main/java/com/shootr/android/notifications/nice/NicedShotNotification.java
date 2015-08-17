package com.shootr.android.notifications.nice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.gcm.NotificationIntentReceiver;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;
import java.io.IOException;
import javax.inject.Inject;

public class NicedShotNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 1;

    private final PicassoWrapper picasso;
    private final ActivityModel activity;
    private final String notificationContentText;
    private final String notificationTextPattern;

    @Inject public NicedShotNotification(Context context,
      NotificationBuilderFactory builderFactory,
      PicassoWrapper picasso,
      ActivityModel activityModel) {
        super(context, builderFactory);
        this.picasso = picasso;
        this.activity = activityModel;
        this.notificationContentText = context.getString(R.string.notification_nice_content_text);
        this.notificationTextPattern = context.getString(R.string.notification_nice_text_pattern);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentIntent(getOpenNicedShotNotificationPendingIntent());
        builder.setContentTitle(activity.getUsername());
        builder.setContentText(getText());
    }

    private String getText() {
        return notificationContentText;
    }

    protected PendingIntent getOpenNicedShotNotificationPendingIntent() {
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN,
          new Intent(NotificationIntentReceiver.ACTION_OPEN_ACTIVITY_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    protected CharSequence getTickerText() {
        return String.format(notificationTextPattern, activity.getUsername());
    }

    @Override
    public Bitmap getLargeIcon() {
        try {
            return picasso.loadTimelineImage(activity.getUserPhoto()).get();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }

}
