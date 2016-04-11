package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.util.ImageLoader;

public class FollowActivityNotification extends SingleActivityNotification {

    private static final int REQUEST_OPEN = 2;

    private final String idUser;

    public FollowActivityNotification(Context context, NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values, String idUser) {
        super(context, builderFactory, imageLoader, values);
        this.idUser = idUser;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentIntent(getOpenProfileNotificationPendingIntent());
    }

    protected PendingIntent getOpenProfileNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_PROFILE);
        intent.putExtra(ProfileContainerActivity.EXTRA_USER, idUser);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
