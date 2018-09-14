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
import com.shootr.mobile.notifications.shot.ShotNotification;
import com.shootr.mobile.ui.activities.NewShotDetailActivity;
import com.shootr.mobile.util.ImageLoader;

public class ShotActivityNotification extends SingleActivityNotification {

  private static final int REQUEST_OPEN = 2;
  private final String idShot;
  private final Boolean updateNeeded;
  private final ShotNotification shotNotification;
  private final Boolean isInApp;

  public ShotActivityNotification(Context context, NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values, String idShot,
      Boolean updateNeeded, ShotNotification shotNotification, Boolean isInApp) {
    super(context, builderFactory, imageLoader, values);
    this.idShot = idShot;
    this.updateNeeded = updateNeeded;
    this.shotNotification = shotNotification;
    this.isInApp = isInApp;
  }

  @Override public void setNotificationValues(final NotificationCompat.Builder builder,
      Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentIntent(getShotNotificationPendingIntent());
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
    if (isInApp) {
      builder.setPriority(Notification.PRIORITY_HIGH);
    }
  }

  private PendingIntent getShotNotificationPendingIntent() {
    if (!updateNeeded) {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_SHOT_DETAIL);
      intent.putExtra(NewShotDetailActivity.EXTRA_ID_SHOT, shotNotification.getIdShot());
      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
    } else {
      Intent intent = new Intent(NotificationIntentReceiver.ACTION_NEED_UPDATE);
      return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
          PendingIntent.FLAG_CANCEL_CURRENT);
    }
  }
}
