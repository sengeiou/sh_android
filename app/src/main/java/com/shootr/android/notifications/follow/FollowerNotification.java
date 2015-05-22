package com.shootr.android.notifications.follow;

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
import com.shootr.android.ui.model.UserModel;

public class FollowerNotification extends CommonNotification {

    private UserModel user;
    private static final int REQUEST_OPEN = 2;
    public FollowerNotification(Context context, NotificationBuilderFactory builderFactory, UserModel user) {
        super(context, builderFactory);
        this.user = user;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(getTitle());
        String message = getResources().getString(R.string.following_notif_text);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentIntent(getOpenProfileNotificationPendingIntent());
    }

    private String getTitle() {
        return getResources().getString(R.string.notification_follow_title, user.getUsername());
    }

    @Override
    protected CharSequence getTickerText() {
        return getTitle();
    }

    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }

    protected PendingIntent getOpenProfileNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_PROFILE);
        intent.putExtra(ProfileContainerActivity.EXTRA_USER,user.getIdUser());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),REQUEST_OPEN,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

}


