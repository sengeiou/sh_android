package com.shootr.mobile.notifications.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.fragments.streamtimeline.TimelineFragment;
import com.shootr.mobile.util.ImageLoader;

public class StreamActivityNotification extends SingleActivityNotification {

  private static final int REQUEST_OPEN = 2;
  private final String idStream;
  private final String idStreamHolder;
  private final String title;
  private final String readWriteMode;
  private final Boolean updateNeeded;
  private final Boolean isInApp;

  public StreamActivityNotification(Context context,
      NotificationBuilderFactory notificationBuilderFactory, ImageLoader imageLoader,
      PushNotification.NotificationValues notificationValues, String idStream,
      String idStreamHolder, String title, String readWriteMode, Boolean updateNeeded,
      Boolean isInApp) {
    super(context, notificationBuilderFactory, imageLoader, notificationValues);
    this.idStream = idStream;
    this.idStreamHolder = idStreamHolder;
    this.title = title;
    this.readWriteMode = readWriteMode;
    this.updateNeeded = updateNeeded;
    this.isInApp = isInApp;
  }

  @Override
  public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentIntent(getOpenStreamTimelineNotificationPendingIntent());
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
    if (isInApp) {
      builder.setPriority(Notification.PRIORITY_HIGH);
    }
  }

  private PendingIntent getOpenStreamTimelineNotificationPendingIntent() {
    if (!updateNeeded) {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_STREAM);
      intent.putExtra(TimelineFragment.EXTRA_STREAM_ID, idStream);
      intent.putExtra(TimelineFragment.EXTRA_STREAM_TITLE, title);

      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
    } else {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_NEED_UPDATE);
      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
    }
  }
}
