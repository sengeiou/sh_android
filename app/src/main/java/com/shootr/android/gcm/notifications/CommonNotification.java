package com.shootr.android.gcm.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;

public abstract class CommonNotification {

    private static final Uri RINGTONE_DEFAULT = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private NotificationBuilderFactory builderFactory;
    private Resources resources;
    private Context context;

    public CommonNotification(Context context, NotificationBuilderFactory builderFactory) {
        this.context = context;
        this.builderFactory = builderFactory;
        resources = context.getResources();
    }

    private void setCommonNotificationValues(NotificationCompat.Builder builder) {
        builder.setSound(getRingtone());
        builder.setPriority(getPriority());
        builder.setSmallIcon(getSmallIcon());
        builder.setLargeIcon(getLargeIcon());
        builder.setAutoCancel(true);
    }

    public abstract void setNotificationValues(NotificationCompat.Builder builder);

    public Notification build() {
        NotificationCompat.Builder builder = builderFactory.getNotificationBuilder(context);
        setCommonNotificationValues(builder);
        setNotificationValues(builder);
        return builder.build();
    }

    protected Resources getResources() {
        return resources;
    }

    protected Context getContext() {
        return context;
    }

    public Uri getRingtone() {
        return RINGTONE_DEFAULT;
    }

    public int getPriority() {
        return NotificationCompat.PRIORITY_LOW;
    }

    @DrawableRes public int getSmallIcon() {
        return R.drawable.ic_ab_icon;
    }

    public abstract Bitmap getLargeIcon();

    public abstract Bitmap getWearBackground();

}
