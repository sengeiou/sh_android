package com.shootr.mobile.notifications.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.notifications.activity.ActivityNotificationManager;
import com.shootr.mobile.notifications.shot.ShotNotificationManager;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String ACTION_OPEN_PROFILE = "com.shootr.mobile.ACTION_OPEN_PROFILE";
    public static final String ACTION_DISCARD_SHOT_NOTIFICATION = "com.shootr.mobile.ACTION_DISCARD_SHOT_NOTIFICATION";
    public static final String ACTION_OPEN_SHOT_NOTIFICATION = "com.shootr.mobile.ACTION_OPEN_SHOT_NOTIFICATION";
    public static final String ACTION_OPEN_ACTIVITY_NOTIFICATION = "com.shootr.mobile.ACTION_OPEN_ACTIVITY_NOTIFICATION";
    public static final String ACTION_DISCARD_ACTIVITY_NOTIFICATION = "com.shootr.mobile.ACTION_DISCARD_ACTIVITY_NOTIFICATION";

    @Inject ShotNotificationManager shotNotificationManager;
    @Inject ActivityNotificationManager activityNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShootrApplication.get(context).inject(this);

        String action = intent.getAction();
        switch (action) {
            case ACTION_DISCARD_SHOT_NOTIFICATION:
                shotNotificationManager.clearShotNotifications();
                break;
            case ACTION_OPEN_SHOT_NOTIFICATION:
                context.startActivity(new Intent(context,
                  MainTabbedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                shotNotificationManager.clearShotNotifications();
                break;
            case ACTION_OPEN_ACTIVITY_NOTIFICATION:
                context.startActivity(new Intent(context,
                  MainTabbedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activityNotificationManager.clearActivityNotifications();
                break;
            case ACTION_DISCARD_ACTIVITY_NOTIFICATION:
                activityNotificationManager.clearActivityNotifications();
                break;
            case ACTION_OPEN_PROFILE:
                String idUser = intent.getExtras().getString(ProfileContainerActivity.EXTRA_USER);
                context.startActivity(ProfileContainerActivity.getIntent(context, idUser)
                  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activityNotificationManager.clearActivityNotifications();
                break;
            default:
                throw new IllegalStateException("Action \"" + action + "\" not handled in " + this.getClass()
                  .getSimpleName());
        }
    }
}
