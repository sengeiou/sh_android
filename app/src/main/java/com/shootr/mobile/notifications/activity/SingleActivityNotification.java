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

    public SingleActivityNotification(Context context, NotificationBuilderFactory builderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues values) {
        super(context, builderFactory);
        this.imageLoader = imageLoader;
        this.values = values;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        super.setNotificationValues(builder, areShotTypesKnown);
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingleActivityNotification that = (SingleActivityNotification) o;

        if (imageLoader != null ? !imageLoader.equals(that.imageLoader)
            : that.imageLoader != null) {
            return false;
        }
        return values != null ? values.equals(that.values) : that.values == null;
    }

    @Override public int hashCode() {
        int result = imageLoader != null ? imageLoader.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
