package com.shootr.mobile.notifications.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.prefs.ActivityBadgeCount;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.notifications.activity.ActivityNotificationManager;
import com.shootr.mobile.notifications.message.MessageNotificationManager;
import com.shootr.mobile.notifications.shot.ShotNotificationManager;
import com.shootr.mobile.ui.activities.ChannelsContainerActivity;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PrivateMessageTimelineActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AnalyticsTool;
import javax.inject.Inject;

public class NotificationIntentReceiver extends BroadcastReceiver {

  public static final String ACTION_OPEN_PROFILE = "com.shootr.mobile.ACTION_OPEN_PROFILE";
  public static final String ACTION_OPEN_STREAM = "com.shootr.mobile.ACTION_OPEN_STREAM";
  public static final String ACTION_OPEN_SHOT_DETAIL = "com.shootr.mobile.ACTION_OPEN_SHOT_DETAIL";
  public static final String ACTION_OPEN_PRIVATE_MESSAGE = "com.shootr.mobile.ACTION_OPEN_PRIVATE_MESSAGE";
  public static final String ACTION_OPEN_CHANNEL_LIST = "com.shootr.mobile.ACTION_OPEN_CHANNEL_LIST";
  public static final String ACTION_DISCARD_PRIVATE_MESSAGE = "com.shootr.mobile.ACTION_DISCARD_PRIVATE_MESSAGE";
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

  private String analyticsActionPushOpen;
  private String analyticsLabelPushOpen;

  @Inject ShotNotificationManager shotNotificationManager;
  @Inject ActivityNotificationManager activityNotificationManager;
  @Inject MessageNotificationManager messageNotificationManager;
  @Inject @ActivityBadgeCount IntPreference badgeCount;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @Override public void onReceive(Context context, Intent intent) {
    ShootrApplication.get(context).inject(this);

    analyticsActionPushOpen = context.getString(R.string.analytics_action_push_open);
    analyticsLabelPushOpen = context.getString(R.string.analytics_label_push_open);
    String pollRedirection = context.getString(R.string.analytics_push_redirection_poll);
    String profileRedirection = context.getString(R.string.analytics_push_redirection_profile);
    String shotDetailRedirection = context.getString(R.string.analytics_push_redirection_shot_detail);
    String streamRedirection = context.getString(R.string.analytics_push_redirection_stream);
    String activityRedirection = context.getString(R.string.analytics_push_redirection_activity);
    String discardRedirection = context.getString(R.string.analytics_push_redirection_discard);
    String needUpdateRedirection = context.getString(R.string.analytics_push_redirection_need_update);
    String privateMessageRedirection = context.getString(R.string.analytics_push_redirection_private_messages);

    String action = intent.getAction();
    switch (action) {
      case ACTION_DISCARD_SHOT_NOTIFICATION:
        shotNotificationManager.clearShotNotifications();
        sendGoogleAnalythicsPushOpen(context, discardRedirection);
        break;
      case ACTION_OPEN_SHOT_NOTIFICATION:
        openActivities(context);
        sendGoogleAnalythicsPushOpen(context, activityRedirection);
        break;
      case ACTION_OPEN_ACTIVITY_NOTIFICATION:
        startActivityFromIntent(context, MainTabbedActivity.getMultipleActivitiesIntent(context)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        sendGoogleAnalythicsPushOpen(context, activityRedirection);
        break;
      case ACTION_DISCARD_ACTIVITY_NOTIFICATION:
        activityNotificationManager.clearActivityNotifications();
        sendGoogleAnalythicsPushOpen(context, discardRedirection);
        break;
      case ACTION_OPEN_PROFILE:
        openProfile(context, intent);
        sendGoogleAnalythicsPushOpen(context, profileRedirection);
        break;
      case ACTION_OPEN_STREAM:
        openStream(context, intent);
        sendGoogleAnalythicsPushOpen(context, streamRedirection);
        break;
      case ACTION_OPEN_SHOT_DETAIL:
        openShotDetail(context, intent);
        sendGoogleAnalythicsPushOpen(context, shotDetailRedirection);
        break;
      case ACTION_OPEN_POLL_VOTE:
        openPollVote(context, intent);
        sendGoogleAnalythicsPushOpen(context, pollRedirection);
        break;
      case ACTION_NEED_UPDATE:
        openUpdateNeeded(context);
        sendGoogleAnalythicsPushOpen(context, needUpdateRedirection);
        break;
      case ACTION_OPEN_PRIVATE_MESSAGE:
        openPrivateMessage(context, intent);
        sendGoogleAnalythicsPushOpen(context, privateMessageRedirection);
        break;
      case ACTION_DISCARD_PRIVATE_MESSAGE:
        messageNotificationManager.clearActivityNotifications();
        sendGoogleAnalythicsPushOpen(context, discardRedirection);
        break;
      case ACTION_OPEN_CHANNEL_LIST:
        openChannelList(context);
        sendGoogleAnalythicsPushOpen(context, privateMessageRedirection);
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
    if (idUser != null) {
      startActivityFromIntent(context,
          ProfileActivity.getIntent(context, idUser).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
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
    String idShot = (String) intent.getExtras().get(ShotDetailActivity.EXTRA_ID_SHOT);
    startActivityFromIntent(context, ShotDetailActivity.getIntentForActivity(context, idShot)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openPollVote(Context context, Intent intent) {
    String idPoll = intent.getStringExtra(PollVoteActivity.EXTRA_ID_POLL);
    String streamTitle = intent.getStringExtra(PollVoteActivity.EXTRA_STREAM_TITLE);
    decrementBadgeCount();
    startActivityFromIntent(context, PollVoteActivity.newIntentWithIdPoll(context, idPoll, streamTitle)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }

  public void openPrivateMessage(Context context, Intent intent) {
    String idTargetUser = intent.getStringExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER);
    startActivityFromIntent(context, PrivateMessageTimelineActivity.newIntent(context, idTargetUser)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    messageNotificationManager.clearActivityNotifications();
  }

  public void openChannelList(Context context) {
    Intent channelIntent = new Intent(context, ChannelsContainerActivity.class);
    startActivityFromIntent(context, channelIntent
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    messageNotificationManager.clearActivityNotifications();
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

  private void sendGoogleAnalythicsPushOpen(Context context, String redirection) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(context);
    builder.setActionId(analyticsActionPushOpen);
    builder.setLabelId(analyticsLabelPushOpen);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setPushRedirection(redirection);
    analyticsTool.analyticsSendAction(builder);
  }
}
