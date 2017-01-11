package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.util.ImageLoader;

public class PrivateMessageActivityNotification extends SingleActivityNotification {

  private static final int REQUEST_OPEN = 2;
  private final String idUser;

  public PrivateMessageActivityNotification(Context context,
      NotificationBuilderFactory builderFactory, ImageLoader imageLoader,
      PushNotification.NotificationValues values, String idUser) {
    super(context, builderFactory, imageLoader, values);
    this.idUser = idUser;
  }

  @Override public void setNotificationValues(final NotificationCompat.Builder builder,
      Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentIntent(getPrivateMessageNotificationPendingIntent());
  }

  private PendingIntent getPrivateMessageNotificationPendingIntent() {
    Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_PRIVATE_MESSAGE);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER, idUser);
    return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }
}
