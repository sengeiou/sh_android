package com.shootr.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import timber.log.Timber;

public class SingleShotNotification extends AbstractShotNotification {

    private static final int DEFAULT_USER_PHOTO_RES = R.drawable.ic_contact_picture_default;

    private ShotModel shot;
    private PicassoWrapper picasso;
    private Bitmap largeIcon;

    public SingleShotNotification(Context context, NotificationBuilderFactory builderFactory, PicassoWrapper picasso,
      ShotModel shot) {
        super(context, builderFactory);
        this.shot = shot;
        this.picasso = picasso;
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
            return picasso.loadTimelineImage(shot.getImage()).get();
        } catch (IOException e) {
            Timber.e(e, "Error obteniendo imagen del shot con id %d y url %s", shot.getIdShot(), shot.getImage());
            return null;
        }
    }

    private String getContent() {
        return getShotText(shot);
    }

    public String getTitle() {
        return shot.getUsername();
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
            if (url.isEmpty()) {
                largeIcon = getDefaultPhoto();
            }
            try {
                largeIcon = picasso.loadProfilePhoto(url).get();
            } catch (IOException | IllegalArgumentException e) {
                Timber.e(e, "Error downloading user photo for a shot notification.");
                largeIcon = getDefaultPhoto();
            }
        }
        return largeIcon;
    }

    private Bitmap getDefaultPhoto() {
        return BitmapFactory.decodeResource(getResources(), DEFAULT_USER_PHOTO_RES);
    }
}
