package com.shootr.mobile.notifications.message;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.util.ImageLoader;
import java.io.IOException;

public class PrivateMessageActivityNotification extends AbstractMessageNotification {

  private static final int REQUEST_OPEN = 2;
  private final String idUser;
  private final String comment;
  private final ImageLoader imageLoader;
  private final PushNotification.NotificationValues values;

  public PrivateMessageActivityNotification(Context context, NotificationBuilderFactory builderFactory,
      String idUser, String comment, ImageLoader imageLoader, PushNotification.NotificationValues values) {
    super(context, builderFactory);
    this.idUser = idUser;
    this.comment = comment;
    this.imageLoader = imageLoader;
    this.values = values;
  }

  @Override public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
    super.setNotificationValues(builder, areShotTypesKnown);
    builder.setContentTitle(getTitle());
    builder.setContentText(comment);
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
    builder.setStyle(new NotificationCompat.BigTextStyle() //
          .bigText(comment));

    builder.setContentIntent(getSingleMessageNotificationPendingIntent());
  }

  public String getTitle() {
    return values.getTitle();
  }

  public String getContentText() {
    return comment;
  }

  @Override public CharSequence getTickerText() {
    return values.getTickerText();
  }

  @Override public Bitmap getLargeIcon() {
    try {
      return imageLoader.load(values.getIcon());
    } catch (IOException e) {
      return null;
    }
  }

  public PushNotification.NotificationValues getNotificationValues() {
    return values;
  }

  private PendingIntent getSingleMessageNotificationPendingIntent() {
    Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_PRIVATE_MESSAGE);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER, idUser);
    return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);
  }

  public String getIdUser() {
    return idUser;
  }
}
