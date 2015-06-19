package com.shootr.android.notifications.checkin;

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
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.PicassoWrapper;
import java.io.IOException;
import timber.log.Timber;

public class CheckinNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 2;
    private final PicassoWrapper picasso;
    private final ActivityModel activity;

    public CheckinNotification(Context context,
      NotificationBuilderFactory builderFactory,
      PicassoWrapper picasso,
      ActivityModel activityModel) {
        super(context, builderFactory);
        this.picasso = picasso;
        this.activity = activityModel;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(activity.getUsername());
        builder.setContentText(activity.getComment());
    }

    @Override
    protected CharSequence getTickerText() {
        return activity.getUsername() + ": " + activity.getComment();
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
