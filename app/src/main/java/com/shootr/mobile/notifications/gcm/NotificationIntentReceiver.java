package com.shootr.mobile.notifications.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.notifications.activity.ActivityNotificationManager;
import com.shootr.mobile.notifications.shot.ShotNotificationManager;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AnalyticsTool;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

  public static final String ACTION_OPEN_PROFILE = "com.shootr.mobile.ACTION_OPEN_PROFILE";
  public static final String ACTION_OPEN_STREAM = "com.shootr.mobile.ACTION_OPEN_STREAM";
  public static final String ACTION_OPEN_SHOT_DETAIL = "com.shootr.mobile.ACTION_OPEN_SHOT_DETAIL";
  public static final String ACTION_DISCARD_SHOT_NOTIFICATION =
      "com.shootr.mobile.ACTION_DISCARD_SHOT_NOTIFICATION";
  public static final String ACTION_OPEN_SHOT_NOTIFICATION =
      "com.shootr.mobile.ACTION_OPEN_SHOT_NOTIFICATION";
  public static final String ACTION_OPEN_ACTIVITY_NOTIFICATION =
      "com.shootr.mobile.ACTION_OPEN_ACTIVITY_NOTIFICATION";
  public static final String ACTION_DISCARD_ACTIVITY_NOTIFICATION =
      "com.shootr.mobile.ACTION_DISCARD_ACTIVITY_NOTIFICATION";

  public static final String ACTION_NEED_UPDATE = "com.shootr.mobile.ACTION_NEED_UPDATE";
  public static final String ACTION_OPEN_POLL_VOTE = "com.shootr.mobile.ACTION_OPEN_POLL_VOTE";

  @BindString(R.string.analytics_action_push_open) String analyticsActionPushOpen;
  @BindString(R.string.analytics_label_push_open) String analyticsLabelPushOpen;

  @Inject ShotNotificationManager shotNotificationManager;
  @Inject ActivityNotificationManager activityNotificationManager;
  @Inject @ActivityBadgeCount IntPreference badgeCount;
  @Inject AnalyticsTool analyticsTool;

  @Override public void onReceive(Context context, Intent intent) {
    ShootrApplication.get(context).inject(this);

    String action = intent.getAction();
    switch (action) {
      case ACTION_DISCARD_SHOT_NOTIFICATION:
        shotNotificationManager.clearShotNotifications();
        break;
      case ACTION_OPEN_SHOT_NOTIFICATION:
        openActivities(context);
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_OPEN_ACTIVITY_NOTIFICATION:
        startActivityFromIntent(context, MainTabbedActivity.getMultipleActivitiesIntent(context)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_DISCARD_ACTIVITY_NOTIFICATION:
        activityNotificationManager.clearActivityNotifications();
        break;
      case ACTION_OPEN_PROFILE:
        openProfile(context, intent);
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_OPEN_STREAM:
        openStream(context, intent);
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_OPEN_SHOT_DETAIL:
        openShotDetail(context, intent);
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_OPEN_POLL_VOTE:
        openPollVote(context, intent);
        sendGoogleAnalythicsPushOpen(context, analyticsActionPushOpen);
        break;
      case ACTION_NEED_UPDATE:
        openUpdateNeeded(context);
        sendGoogleAnalythicsPushOpen(context, ACTION_NEED_UPDATE);
        break;
      default:
        openUpdateNeeded(context);
        break;
    }
  }

  public void openActivities(Context context) {
    context.startActivity(MainTabbedActivity.getMultipleActivitiesIntent(context)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    shotNotificationManager.clearShotNotifications();
  }

  public void openProfile(Context context, Intent intent) {
    decrementBadgeCount();
    String idUser = intent.getExtras().getString(ProfileActivity.EXTRA_USER);
    startActivityFromIntent(context, ProfileActivity.getIntent(context, idUser)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openStream(Context context, Intent intent) {
    decrementBadgeCount();
    String idStream = intent.getExtras().getString(StreamTimelineFragment.EXTRA_STREAM_ID);
    String idStreamHolder = intent.getExtras().getString(StreamTimelineFragment.EXTRA_ID_USER);
    String title = intent.getExtras().getString(StreamTimelineFragment.EXTRA_STREAM_TITLE);
    startActivityFromIntent(context,
        StreamTimelineActivity.newIntent(context, idStream, title, idStreamHolder)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openShotDetail(Context context, Intent intent) {
    decrementBadgeCount();
    ShotModel shotModel = (ShotModel) intent.getExtras().get(ShotDetailActivity.EXTRA_SHOT);
    startActivityFromIntent(context, ShotDetailActivity.getIntentForActivity(context, shotModel)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openPollVote(Context context, Intent intent) {
    String idPoll = intent.getStringExtra(PollVoteActivity.EXTRA_ID_POLL);
    decrementBadgeCount();
    startActivityFromIntent(context, PollVoteActivity.newIntentWithIdPoll(context, idPoll)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openUpdateNeeded(Context context) {
    decrementBadgeCount();
    startActivityFromIntent(context,
        MainTabbedActivity.getUpdateNeededIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void startActivityFromIntent(Context context, Intent intent) {
    context.startActivity(intent);
    activityNotificationManager.clearActivityNotifications();
  }

  private void decrementBadgeCount() {
    int numberOfActivities = badgeCount.get();
    badgeCount.set(numberOfActivities - 1);
  }

  private void sendGoogleAnalythicsPushOpen(Context context, String action) {
    analyticsTool.analyticsSendAction(context, action, analyticsActionPushOpen, analyticsLabelPushOpen);
  }
}
