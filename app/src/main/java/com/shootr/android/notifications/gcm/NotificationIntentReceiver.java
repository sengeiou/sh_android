package com.shootr.android.notifications.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.android.ShootrApplication;
import com.shootr.android.notifications.shot.ShotNotificationManager;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.TimelineActivity;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String ACTION_OPEN_PROFILE = "com.shootr.android.ACTION_OPEN_PROFILE";
    public static final String ACTION_DISCARD_SHOT_NOTIFICATION = "com.shootr.android.ACTION_DISCARD_SHOT_NOTIFICATION";
    public static final String ACTION_OPEN_SHOT_NOTIFICATION = "com.shootr.android.ACTION_OPEN_SHOT_NOTIFICATION";
    public static final String ACTION_OPEN_WATCH_REQUEST = "com.shootr.android.ACTION_OPEN_WATCH_REQUEST";

    @Inject ShotNotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShootrApplication.get(context).inject(this);

        String action = intent.getAction();
        if (action.equals(ACTION_DISCARD_SHOT_NOTIFICATION)) {
            notificationManager.clearShotNotifications();
        }else if (action.equals(ACTION_OPEN_SHOT_NOTIFICATION)) {
            context.startActivity(new Intent(context, TimelineActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            notificationManager.clearShotNotifications();
        } else if (action.equals(ACTION_OPEN_WATCH_REQUEST)) {
            context.startActivity(new Intent(context, TimelineActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else if(action.equals(ACTION_OPEN_PROFILE)){
            Long idUser = intent.getExtras().getLong(ProfileContainerActivity.EXTRA_USER);
            context.startActivity(
              ProfileContainerActivity.getIntent(context, idUser).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}