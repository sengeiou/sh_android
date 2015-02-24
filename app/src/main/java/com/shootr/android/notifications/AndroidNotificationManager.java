package com.shootr.android.notifications;

import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.NotificationsEnabled;
import javax.inject.Inject;
import timber.log.Timber;

public class AndroidNotificationManager {

    private final NotificationManagerCompat notificationManager;
    private final BooleanPreference notificationsEnabled;

    @Inject public AndroidNotificationManager(NotificationManagerCompat notificationManager, @NotificationsEnabled
    BooleanPreference notificationsEnabled) {
        this.notificationManager = notificationManager;
        this.notificationsEnabled = notificationsEnabled;
    }

    public int notify(CommonNotification notification) {
        if (notificationsEnabled.get()) {
            Notification androidNotification = notification.build();
            int notificationId = notification.getId();
            notificationManager.notify(notificationId, androidNotification);
            return notificationId;
        } else {
            Timber.d("Notification not shown. They are disabled.");
            return -1;
        }
    }

    public void removeNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }
}
