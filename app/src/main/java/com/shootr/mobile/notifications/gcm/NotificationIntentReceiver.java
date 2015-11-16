package com.shootr.mobile.notifications.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.notifications.activity.ActivityNotificationManager;
import com.shootr.mobile.notifications.shot.ShotNotificationManager;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.ui.model.ShotModel;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

    public static final String ACTION_OPEN_PROFILE = "com.shootr.mobile.ACTION_OPEN_PROFILE";
    public static final String ACTION_OPEN_STREAM = "com.shootr.mobile.ACTION_OPEN_STREAM";
    public static final String ACTION_OPEN_SHOT_DETAIL = "com.shootr.mobile.ACTION_OPEN_SHOT_DETAIL";
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
            case ACTION_OPEN_STREAM:
                String idStream = intent.getExtras().getString(StreamTimelineFragment.EXTRA_STREAM_ID);
                String idStreamHolder = intent.getExtras().getString(StreamTimelineFragment.EXTRA_ID_USER);
                String shortTitle = intent.getExtras().getString(StreamTimelineFragment.EXTRA_STREAM_SHORT_TITLE);
                context.startActivity(StreamTimelineActivity.newIntent(context, idStream, shortTitle, idStreamHolder)
                  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activityNotificationManager.clearActivityNotifications();
                break;
            case ACTION_OPEN_SHOT_DETAIL:
                ShotModel shotModel = (ShotModel) intent.getExtras().get(ShotDetailActivity.EXTRA_SHOT);
                context.startActivity(ShotDetailActivity.getIntentForActivity(context, shotModel)
                  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                activityNotificationManager.clearActivityNotifications();
                break;
            default:
                throw new IllegalStateException("Action \"" + action + "\" not handled in " + this.getClass()
                  .getSimpleName());
        }
    }
}
