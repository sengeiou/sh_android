package com.shootr.android.notifications.shot;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.notifications.NotificationBuilderFactory;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.ImageLoader;
import java.io.IOException;
import timber.log.Timber;

public class SingleShotNotification extends AbstractShotNotification {

    private ShotModel shot;
    private ImageLoader imageLoader;
    private Bitmap largeIcon;

    public SingleShotNotification(Context context, NotificationBuilderFactory builderFactory, ImageLoader imageLoader,
      ShotModel shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.imageLoader = imageLoader;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentTitle(getTitle());
        builder.setContentText(getContent());
        if (shot.getImage() == null) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getContent()));
        } else {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getImageBitmap()));
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
        return getShotText(shot);
    }

    public String getTitle() {
        if (shot.isReply()) {
            return getResources().getString(R.string.reply_name_pattern, shot.getUsername(), shot.getReplyUsername());
        } else {
            return shot.getUsername();
        }
    }

    @Override
    protected CharSequence getTickerText() {
        return getTitle()+": "+getContent();
    }

    @Override
    public Bitmap getLargeIcon() {
        return getUserPhoto(shot.getPhoto());
    }

    @Override
    public Bitmap getWearBackground() {
        return getUserPhoto(shot.getPhoto());
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
