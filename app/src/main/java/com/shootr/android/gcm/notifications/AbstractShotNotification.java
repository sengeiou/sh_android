package gm.mobi.android.gcm.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import gm.mobi.android.gcm.NotificationIntentReceiver;
import gm.mobi.android.ui.activities.MainActivity;

public abstract class AbstractShotNotification extends CommonNotification {

    private static final int REQUEST_DELETE = 1;
    private static final int REQUEST_OPEN = 2;

    protected AbstractShotNotification(Context context, NotificationBuilderFactory builderFactory) {
        super(context, builderFactory);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentIntent(getOpenShotNotificationPendingIntent());
        builder.setDeleteIntent(getDiscardShotNotificationPendingIntent());
    }

    protected PendingIntent getOpenShotNotificationPendingIntent() {
        Bundle extras = new Bundle();
        extras.putInt(MainActivity.EXTRA_SHOW_SECTION,
          MainActivity.DRAWER_POSITION_TIMELINE);

        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN,
          new Intent(NotificationIntentReceiver.ACTION_OPEN_SHOT_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    protected PendingIntent getDiscardShotNotificationPendingIntent() {
        return PendingIntent.getBroadcast(getContext(), REQUEST_DELETE,
          new Intent(NotificationIntentReceiver.ACTION_DISCARD_SHOT_NOTIFICATION), 0);
    }
}
