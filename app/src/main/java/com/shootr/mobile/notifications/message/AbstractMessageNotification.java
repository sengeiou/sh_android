package com.shootr.mobile.notifications.message;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;

public abstract class AbstractMessageNotification extends CommonNotification {

  private static final int REQUEST_OPEN = 1;
  private static final int REQUEST_DELETE = 2;

  public AbstractMessageNotification(Context context, NotificationBuilderFactory builderFactory) {
    super(context, builderFactory);
  }

  @Override public void setNotificationValues(NotificationCompat.Builder builder,
      Boolean areShotTypesKnown) {
    builder.setContentIntent(getPrivateMessageNotificationPendingIntent());
    builder.setDeleteIntent(getDiscardActivityNotificationPendingIntent());
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
  }

  protected PendingIntent getPrivateMessageNotificationPendingIntent() {
    Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
        NotificationIntentReceiver.ACTION_OPEN_CHANNEL_LIST);
    return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }

  protected PendingIntent getDiscardActivityNotificationPendingIntent() {
    Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
        NotificationIntentReceiver.ACTION_DISCARD_PRIVATE_MESSAGE);
    return PendingIntent.getBroadcast(getContext(), REQUEST_DELETE, intent, 0);
  }
}

