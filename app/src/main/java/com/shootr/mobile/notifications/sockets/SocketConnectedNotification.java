package com.shootr.mobile.notifications.sockets;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;

public class SocketConnectedNotification extends CommonNotification {

  public SocketConnectedNotification(Context context, NotificationBuilderFactory builderFactory) {
    super(context, builderFactory);
  }

  @Override protected CharSequence getTickerText() {
    return "SocketEntity connected";
  }

  @Override
  public void setNotificationValues(NotificationCompat.Builder builder, Boolean areShotTypesKnown) {
    builder.setContentTitle(getTickerText());
    builder.setContentText("Running");
    builder.setOngoing(true);
    builder.setPriority(NotificationCompat.PRIORITY_LOW);
    builder.setSound(null);
    builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
  }

  @Override public Bitmap getLargeIcon() {
    return null;
  }
}
