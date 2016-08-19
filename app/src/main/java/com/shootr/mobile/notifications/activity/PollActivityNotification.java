package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.util.ImageLoader;

public class PollActivityNotification extends SingleActivityNotification {

  private static final int REQUEST_OPEN = 2;

  private String idPoll;
  private Boolean updateNeeded;

  public PollActivityNotification(Context context, String idPoll, NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values, Boolean updateNeeded) {
    super(context, builderFactory, imageLoader, values);
    this.idPoll = idPoll;
    this.updateNeeded = updateNeeded;
  }

  @Override public void setNotificationValues(NotificationCompat.Builder builder,
      Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentIntent(getOpenStreamTimelineNotificationPendingIntent());
  }

  private PendingIntent getOpenStreamTimelineNotificationPendingIntent() {
    if (!updateNeeded) {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_POLL_VOTE);
      intent.putExtra(PollVoteActivity.EXTRA_ID_POLL, idPoll);
      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    } else {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_NEED_UPDATE);
      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
  }

}
