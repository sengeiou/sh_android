package com.shootr.mobile.notifications.shot;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.ui.model.ShotModel;

public abstract class AbstractShotNotification extends CommonNotification {

    private static final int REQUEST_DELETE = 1;
    private static final int REQUEST_OPEN = 2;
    protected String imageOnlyText;
    protected String imageAndTextPattern;

    protected AbstractShotNotification(Context context, NotificationBuilderFactory builderFactory) {
        super(context, builderFactory);
        imageOnlyText = context.getResources().getString(R.string.notification_image);
        imageAndTextPattern = context.getResources().getString(R.string.notification_image_and_text);
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentIntent(getOpenShotNotificationPendingIntent());
        builder.setDeleteIntent(getDiscardShotNotificationPendingIntent());
    }

    protected PendingIntent getOpenShotNotificationPendingIntent() {
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN,
          new Intent(NotificationIntentReceiver.ACTION_OPEN_SHOT_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    protected PendingIntent getDiscardShotNotificationPendingIntent() {
        return PendingIntent.getBroadcast(getContext(), REQUEST_DELETE,
          new Intent(NotificationIntentReceiver.ACTION_DISCARD_SHOT_NOTIFICATION), 0);
    }

    protected String getShotText(ShotModel shot) {
        if (shot.getImage() == null) {
            return shot.getComment();
        } else {
            return getContentTextWithImage(shot);
        }
    }

    private String getContentTextWithImage(ShotModel shot) {
        if (shot.getComment() == null) {
            return getImageOnlyContent();
        } else {
            return getImageAndTextContent(shot);
        }
    }

    private String getImageOnlyContent() {
        return imageOnlyText;
    }

    private String getImageAndTextContent(ShotModel shot) {
        return String.format(imageAndTextPattern, shot.getComment());
    }
}
