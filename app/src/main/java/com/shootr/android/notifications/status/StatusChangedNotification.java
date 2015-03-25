package com.shootr.android.notifications.status;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.notifications.CommonNotification;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.notifications.gcm.NotificationIntentReceiver;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import java.io.IOException;
import timber.log.Timber;

public class StatusChangedNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 2;

    private static final int DEFAULT_USER_PHOTO_RES = R.drawable.ic_contact_picture_default;
    private PicassoWrapper picasso;
    private Bitmap largeIcon;
    private final UserModel userModel;
    private final String newStatus;

    private final String statusFormat;

    public StatusChangedNotification(Context context, NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso, UserModel userModel, String newStatus) {
        super(context,notificationBuilderFactory);
        this.picasso = picasso;
        this.userModel = userModel;
        this.newStatus = newStatus;

        statusFormat = context.getString(R.string.notification_status_changed);
        if (newStatus == null) {
            throw new RuntimeException("Not allowed to receive null statuses");
        }
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(getTitle());
        String message = getMessage();
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentIntent(getOpenWatchRequestPendingIntent());
    }

    private String getTitle() {
        return userModel.getUsername();
    }

    protected Bitmap getUserPhoto(String url) {
        if (largeIcon == null) {
            if (url.isEmpty()) {
                largeIcon = getDefaultPhoto();
            }else{
                try {
                    largeIcon = picasso.loadProfilePhoto(url).get();
                } catch (IOException | IllegalArgumentException e) {
                    Timber.e(e, "Error downloading user photo for a shot notification.");
                    largeIcon = getDefaultPhoto();
                }
            }
        }
        return largeIcon;
    }

    protected PendingIntent getOpenWatchRequestPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_STATUS_CHANGED);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public Bitmap getLargeIcon() {
        return getUserPhoto(userModel.getPhoto());
    }

    @Override
    public Bitmap getWearBackground() {
        return getUserPhoto(userModel.getPhoto());
    }

    private Bitmap getDefaultPhoto() {
        return BitmapFactory.decodeResource(getResources(), DEFAULT_USER_PHOTO_RES);
    }

    @DrawableRes public int getSmallIcon() {
        return R.drawable.ic_ab_icon;
    }

    public String getMessage() {
        return String.format(statusFormat, newStatus);
    }
}
