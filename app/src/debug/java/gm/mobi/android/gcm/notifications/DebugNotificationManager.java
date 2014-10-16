package gm.mobi.android.gcm.notifications;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import com.squareup.picasso.Picasso;
import gm.mobi.android.data.NotificationsEnabled;
import gm.mobi.android.data.prefs.BooleanPreference;
import javax.inject.Inject;

public class DebugNotificationManager extends BagdadNotificationManager {

    private BooleanPreference notificationsEnabled;

    @Inject public DebugNotificationManager(Application context, NotificationManagerCompat notificationManager, Picasso picasso, @NotificationsEnabled
      BooleanPreference notificationsEnabled) {
        super(context, notificationManager, picasso);
        this.notificationsEnabled = notificationsEnabled;
    }

    @Override public boolean areNotificationsEnabled() {
        return notificationsEnabled.get();
    }
}
