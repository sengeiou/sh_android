package com.shootr.mobile.notifications.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.util.ImageLoader;
import java.io.IOException;

public class SingleActivityNotification extends AbstractActivityNotification {

    private final ImageLoader imageLoader;

    private final PushNotification.NotificationValues values;

    public SingleActivityNotification(Context context,
      NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader,
      PushNotification.NotificationValues values) {
        super(context, builderFactory);
        this.imageLoader = imageLoader;
        this.values = values;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentTitle(getTitle());
        builder.setContentText(getContentText());
        if (values.getOptionalLongText() != null) {
            builder.setStyle(new NotificationCompat.BigTextStyle() //
              .bigText(values.getOptionalLongText()) //
              .setSummaryText(getContentText()));
        }
    }

    public String getTitle() {
        return values.getTitle();
    }

    public String getContentText() {
        return values.getContentText();
    }

    @Override
    public CharSequence getTickerText() {
        return values.getTickerText();
    }

    @Override
    public Bitmap getLargeIcon() {
        try {
            return imageLoader.load(values.getIcon());
        } catch (IOException e) {
            return null;
        }
    }

    public PushNotification.NotificationValues getNotificationValues() {
        return values;
    }
}
