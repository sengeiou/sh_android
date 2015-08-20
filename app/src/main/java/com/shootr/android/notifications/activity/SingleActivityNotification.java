package com.shootr.android.notifications.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.gcm.PushNotification;
import com.shootr.android.util.PicassoWrapper;
import java.io.IOException;

public class SingleActivityNotification extends AbstractActivityNotification {

    private final PicassoWrapper picasso;
    private final PushNotification.NotificationValues values;

    public SingleActivityNotification(Context context,
      NotificationBuilderFactory builderFactory,
      PicassoWrapper picasso, PushNotification.NotificationValues values) {
        super(context, builderFactory);
        this.picasso = picasso;
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
            return picasso.loadTimelineImage(values.getIcon()).get();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }
}
