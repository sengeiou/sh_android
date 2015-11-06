package com.shootr.mobile.notifications;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilderFactory {

    public NotificationCompat.Builder getNotificationBuilder(Context context) {
        return new NotificationCompat.Builder(context);
    }

}
