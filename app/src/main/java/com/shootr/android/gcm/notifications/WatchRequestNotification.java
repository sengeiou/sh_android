package com.shootr.android.gcm.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.gcm.NotificationIntentReceiver;
import com.shootr.android.ui.model.UserWatchingModel;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import timber.log.Timber;

public class WatchRequestNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 2;
    String text;
    UserWatchingModel userWatchingModel;
    private static final int DEFAULT_USER_PHOTO_RES = R.drawable.ic_contact_picture_default;
    private Picasso picasso;
    private Bitmap largeIcon;


    public WatchRequestNotification(Context context, NotificationBuilderFactory notificationBuilderFactory, String text,
      UserWatchingModel userWatchingModel, Picasso picasso) {
        super(context,notificationBuilderFactory);
        this.text = text;
        this.userWatchingModel = userWatchingModel;
        this.picasso = picasso;

    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {

        builder.setContentTitle(userWatchingModel.getUserName());
        builder.setContentText(text);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        builder.setContentIntent(getOpenWatchRequestPendingIntent());
    }

    protected Bitmap getUserPhoto(String url) {
        if (largeIcon == null) {
            if (url.isEmpty()) {
                largeIcon = getDefaultPhoto();
            }
            try {
                largeIcon = picasso.load(url).get();
            } catch (IOException | IllegalArgumentException e) {
                Timber.e(e, "Error downloading user photo for a shot notification.");
                largeIcon = getDefaultPhoto();
            }
        }
        return largeIcon;
    }

    protected PendingIntent getOpenWatchRequestPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_WATCH_REQUEST);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),REQUEST_OPEN,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    @Override
    public Bitmap getLargeIcon() {
        return getUserPhoto(userWatchingModel.getPhoto());
    }

    @Override
    public Bitmap getWearBackground() {
        return getUserPhoto(userWatchingModel.getPhoto());
    }


    private Bitmap getDefaultPhoto() {
        return BitmapFactory.decodeResource(getResources(), DEFAULT_USER_PHOTO_RES);
    }

    @DrawableRes public int getSmallIcon() {
        return R.drawable.ic_ab_icon;
    }
}
