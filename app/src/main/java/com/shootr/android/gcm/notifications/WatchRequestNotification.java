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
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import timber.log.Timber;

public class WatchRequestNotification extends CommonNotification {

    private static final int REQUEST_OPEN = 2;


    private UserWatchingModel userWatchingModel;
    private static final int DEFAULT_USER_PHOTO_RES = R.drawable.ic_contact_picture_default;
    private PicassoWrapper picasso;
    private MatchModel matchModel;
    private Bitmap largeIcon;

    private final String formatWatching;
    private final String formatWatchingPlace;
    private final String formatNotWatching;

    public WatchRequestNotification(Context context, NotificationBuilderFactory notificationBuilderFactory,
      PicassoWrapper picasso, UserWatchingModel userWatchingModel, MatchModel matchModel) {
        super(context,notificationBuilderFactory);
        this.userWatchingModel = userWatchingModel;
        this.matchModel = matchModel;
        this.picasso = picasso;

        formatWatching = context.getString(R.string.notification_watch_watching);
        formatWatchingPlace = context.getString(R.string.notification_watch_watching_place);
        formatNotWatching = context.getString(R.string.notification_watch_not_watching);
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(userWatchingModel.getUserName());
        String message = getMessage();
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentIntent(getOpenWatchRequestPendingIntent());
    }

    protected Bitmap getUserPhoto(String url) {
        if (largeIcon == null) {
            if (url.isEmpty()) {
                largeIcon = getDefaultPhoto();
            }else{
                try {
                    largeIcon = picasso.load(url).get();
                } catch (IOException | IllegalArgumentException e) {
                    Timber.e(e, "Error downloading user photo for a shot notification.");
                    largeIcon = getDefaultPhoto();
                }
            }
        }
        return largeIcon;
    }

    protected PendingIntent getOpenWatchRequestPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_WATCH_REQUEST);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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

    public String getMessage() {
        if (userWatchingModel.isWatching()) {
            return getWatchingMessage();
        } else {
            return getNotWatchingMessage();
        }
    }

    public String getWatchingMessage() {
        if (userWatchingModel.getPlace() != null) {
            return String.format(formatWatchingPlace, matchModel.getTitle(), userWatchingModel.getPlace());
        } else {
            return String.format(formatWatching, matchModel.getTitle());
        }
    }

    private String getNotWatchingMessage() {
        return String.format(formatNotWatching, matchModel.getTitle());
    }


}
