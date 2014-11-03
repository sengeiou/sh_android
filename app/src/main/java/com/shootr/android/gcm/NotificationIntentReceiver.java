package com.shootr.android.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.android.ShootrApplication;
import com.shootr.android.gcm.notifications.ShootrNotificationManager;
import com.shootr.android.ui.activities.MainActivity;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String ACTION_DISCARD_SHOT_NOTIFICATION = "com.shootr.android.ACTION_DISCARD_SHOT_NOTIFICATION";
    public static final String ACTION_OPEN_SHOT_NOTIFICATION = "com.shootr.android.ACTION_OPEN_SHOT_NOTIFICATION";

    @Inject ShootrNotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShootrApplication.get(context).inject(this);

        String action = intent.getAction();
        if (action.equals(ACTION_DISCARD_SHOT_NOTIFICATION)) {
            notificationManager.clearShotNotifications();
        }else if (action.equals(ACTION_OPEN_SHOT_NOTIFICATION)) {
            context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            notificationManager.clearShotNotifications();
        }
    }
}
