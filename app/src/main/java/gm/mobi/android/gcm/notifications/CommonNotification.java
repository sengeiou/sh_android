package gm.mobi.android.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class CommonNotification {

    Uri ringtone;
    NotificationCompat.Builder builder;

    public CommonNotification(Context context) {
        builder = new NotificationCompat.Builder(context);
    }

    public CommonNotification() {

    }

    public NotificationCompat.Builder getBuilder() {
        return null;
    }

    public void setNotificationValues(NotificationCompat.Builder builder) {

    }



}
