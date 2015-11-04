package com.shootr.mobile.notifications;

import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.NotificationsEnabled;
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

    public void notify(CommonNotification notification, String tag, int id) {
        if (notificationsEnabled.get()) {
            Notification androidNotification = notification.build();
            notificationManager.notify(tag, id, androidNotification);
        } else {
            Timber.w("Notification not shown. They are disabled.");
        }
    }

    public void removeNotification(String tag, int notificationId) {
        notificationManager.cancel(tag, notificationId);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }
}
