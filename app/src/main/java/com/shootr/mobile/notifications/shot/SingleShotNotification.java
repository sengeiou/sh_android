package com.shootr.mobile.notifications.shot;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.shootr.mobile.R;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.util.ImageLoader;
import java.io.IOException;
import timber.log.Timber;

public class SingleShotNotification extends AbstractSingleShotNotification {

    private final Boolean areShotTypesKnown;
    private final Boolean isInApp;
    private ShotNotification shot;
    private ImageLoader imageLoader;
    private Bitmap largeIcon;


    public SingleShotNotification(Context context, NotificationBuilderFactory builderFactory,
        ImageLoader imageLoader, ShotNotification shot, Boolean areShotTypesKnown, Boolean isInApp) {
        super(context, builderFactory, shot);
        this.shot = shot;
        this.imageLoader = imageLoader;
        this.areShotTypesKnown = areShotTypesKnown;
        this.isInApp = isInApp;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder,
        Boolean areShotTypesKnown) {
        super.setNotificationValues(builder, this.areShotTypesKnown);
        builder.setContentTitle(getTitle());
        builder.setContentText(getContent());
        builder.setColor(ContextCompat.getColor(getContext(), R.color.shootr_orange));
        if (shot.getImage() == null) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getContent()));
        } else {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getImageBitmap()));
        }
        if (isInApp) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
    }

    private Bitmap getImageBitmap() {
        try {
            return imageLoader.load(shot.getImage());
        } catch (IOException e) {
            Timber.e(e, "Error obteniendo imagen del shot con id %d y url %s", shot.getIdShot(), shot.getImage());
            return null;
        }
    }

    private String getContent() {
        return shot.getContentText();
    }

    public String getTitle() {
        return shot.getTitle();
    }

    @Override protected CharSequence getTickerText() {
        return getTitle() + ": " + getContent();
    }

    @Override public Bitmap getLargeIcon() {
        return getUserPhoto(shot.getAvatarImage());
    }

    protected Bitmap getUserPhoto(String url) {
        if (largeIcon == null) {
            try {
                largeIcon = imageLoader.loadProfilePhoto(url);
            } catch (IOException | IllegalArgumentException e) {
                Timber.e(e, "Error downloading user photo for a shot notification.");
            }
        }
        return largeIcon;
    }
}
