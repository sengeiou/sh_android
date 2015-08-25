package com.shootr.android.notifications.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.gcm.PushNotification;
import com.shootr.android.util.ImageLoader;
import java.io.IOException;

public class SingleActivityNotification extends AbstractActivityNotification {

    private final ImageLoader imageLoader;
    private final PushNotification.NotificationValues values;

    public SingleActivityNotification(Context context,
      NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values) {
        super(context, builderFactory);
        this.imageLoader = imageLoader;
        this.values = values;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentTitle(getTitle());
        builder.setContentText(getContentText());
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
            return imageLoader.loadProfilePhoto(values.getIcon());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }
}
