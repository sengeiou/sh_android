package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.util.ImageLoader;

public class FollowActivityNotification extends SingleActivityNotification {

    private static final int REQUEST_OPEN = 2;

    private final String idUser;

    public FollowActivityNotification(Context context, NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values, String idUser) {
        super(context, builderFactory, imageLoader, values);
        this.idUser = idUser;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        super.setNotificationValues(builder, areShotTypesKnown);
        builder.setContentIntent(getOpenProfileNotificationPendingIntent());
        builder.setColor(ContextCompat.getColor(getContext(), R.color.primary_selector));
    }

    protected PendingIntent getOpenProfileNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_PROFILE);
        intent.putExtra(ProfileActivity.EXTRA_USER, idUser);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
