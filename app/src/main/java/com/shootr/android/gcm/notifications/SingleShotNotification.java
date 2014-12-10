package com.shootr.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Picasso;
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
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getContent()));
    }

    private String getContent() {
        return shot.getComment();
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
            try {
                largeIcon = picasso.load(url).get();
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
