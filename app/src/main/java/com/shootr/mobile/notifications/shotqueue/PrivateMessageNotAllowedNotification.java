package com.shootr.mobile.notifications.shotqueue;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.ui.activities.DraftsActivity;

public class PrivateMessageNotAllowedNotification extends CommonNotification {

  private String titleText;

  public PrivateMessageNotAllowedNotification(Context context, NotificationBuilderFactory builderFactory) {
    super(context, builderFactory);
    this.titleText = context.getResources().getString(R.string.notification_private_message_not_allowed);
  }

  @Override
  public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
    builder.setContentTitle(titleText);
    builder.setContentText(titleText);
    builder.setSound(null);
    PendingIntent draftsPendingIntent = getOpenIntent();
    builder.setContentIntent(draftsPendingIntent);
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
  }

  protected PendingIntent getOpenIntent() {
    Intent draftsIntent = new Intent(getContext(), DraftsActivity.class);
    return PendingIntent.getActivity(getContext(), 0, draftsIntent, 0);
  }

  @Override
  public Bitmap getLargeIcon() {
    return null;
  }

  @Override
  protected CharSequence getTickerText() {
    return titleText;
  }
}
