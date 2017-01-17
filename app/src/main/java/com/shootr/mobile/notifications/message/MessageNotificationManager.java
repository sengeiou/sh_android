package com.shootr.mobile.notifications.message;

import android.app.Application;
import android.content.Context;
import com.shootr.mobile.notifications.AndroidNotificationManager;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.util.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class MessageNotificationManager {

  private static final String NOTIFICATION_TAG = "messages";
  private static final int MESSAGES_NOTIFICATION_ID = 1;

  private final Context context;
  private final AndroidNotificationManager androidNotificationManager;
  private final NotificationBuilderFactory notificationBuilderFactory;
  private final ImageLoader imageLoader;
  private final List<PrivateMessageActivityNotification> activeNotifications = new ArrayList<>();

  @Inject public MessageNotificationManager(Application context,
      AndroidNotificationManager androidNotificationManager,
      NotificationBuilderFactory notificationBuilderFactory, ImageLoader imageLoader) {
    this.context = context;
    this.androidNotificationManager = androidNotificationManager;
    this.notificationBuilderFactory = notificationBuilderFactory;
    this.imageLoader = imageLoader;
  }

  public void sendOpenPrivateMessageNotification(PushNotification.NotificationValues values,
      String idUser, String comment) {
    PrivateMessageActivityNotification notification =
        new PrivateMessageActivityNotification(context, notificationBuilderFactory, idUser, comment,
            imageLoader, values);
    showNotification(notification);
  }

  private void showNotification(PrivateMessageActivityNotification singleActivityNotification) {
    activeNotifications.add(singleActivityNotification);
    CommonNotification finalNotification;
    if (activeNotifications.size() > 1) {
      finalNotification =
          new MultipleMessageNotification(context, imageLoader, notificationBuilderFactory,
              activeNotifications);
    } else {
      finalNotification = singleActivityNotification;
    }
    androidNotificationManager.notify(finalNotification, NOTIFICATION_TAG, MESSAGES_NOTIFICATION_ID);
  }

  public void clearActivityNotifications() {
    androidNotificationManager.removeNotification(NOTIFICATION_TAG, MESSAGES_NOTIFICATION_ID);
    activeNotifications.clear();
  }
}
