package com.shootr.android.gcm.notifications;

import android.app.Application;
import android.support.v4.app.NotificationManagerCompat;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Picasso;
import com.shootr.android.data.NotificationsEnabled;
import com.shootr.android.data.prefs.BooleanPreference;
import javax.inject.Inject;

public class DebugNotificationManager extends ShootrNotificationManager {

    private BooleanPreference notificationsEnabled;

    @Inject public DebugNotificationManager(Application context,
      NotificationManagerCompat notificationManager,
      NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso,
      @NotificationsEnabled BooleanPreference notificationsEnabled) {
        super(context, notificationManager, notificationBuilderFactory, picasso);
        this.notificationsEnabled = notificationsEnabled;
    }

    @Override public boolean areNotificationsEnabled() {
        return notificationsEnabled.get();
    }
}
