package com.shootr.mobile.notifications.shot;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.CommonNotification;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.model.ShotModel;

public abstract class AbstractSingleShotNotification extends CommonNotification {

    private static final int REQUEST_DELETE = 1;
    private static final int REQUEST_OPEN = 2;
    private final ShotNotification shotNotification;
    protected String imageOnlyText;
    protected String imageAndTextPattern;

    protected AbstractSingleShotNotification(Context context, NotificationBuilderFactory builderFactory,
      ShotNotification shotModel) {
        super(context, builderFactory);
        imageOnlyText = context.getResources().getString(R.string.notification_image);
        imageAndTextPattern = context.getResources().getString(R.string.notification_image_and_text);
        this.shotNotification = shotModel;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        if (areShotTypesKnown) {
            builder.setContentIntent(getOpenShotNotificationPendingIntent());
        } else {
            builder.setContentIntent(getOpenUpdateNeededPendingIntent());
        }
        builder.setDeleteIntent(getDiscardShotNotificationPendingIntent());
        builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
    }

    protected PendingIntent getOpenShotNotificationPendingIntent() {
        Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
            NotificationIntentReceiver.ACTION_OPEN_SHOT_DETAIL);
        intent.putExtra(ShotDetailActivity.EXTRA_ID_SHOT, shotNotification.getIdShot());
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent,
            PendingIntent.FLAG_CANCEL_CURRENT);
    }

    protected PendingIntent getOpenUpdateNeededPendingIntent() {
        Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
            NotificationIntentReceiver.ACTION_NEED_UPDATE);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    protected PendingIntent getDiscardShotNotificationPendingIntent() {
        Intent intent = new Intent(getContext(), NotificationIntentReceiver.class).setAction(
            NotificationIntentReceiver.ACTION_DISCARD_SHOT_NOTIFICATION);
        return PendingIntent.getBroadcast(getContext(), REQUEST_DELETE, intent, 0);
    }

    protected String getShotText(ShotModel shot) {
        if (shot.getImage().getImageUrl() == null) {
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
