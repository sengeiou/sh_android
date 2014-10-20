package gm.mobi.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.ui.model.ShotVO;
import java.io.IOException;
import timber.log.Timber;

public class SingleShotNotification extends AbstractShotNotification {

    private static final int DEFAULT_USER_PHOTO_RES = R.drawable.ic_contact_picture_default;

    private ShotVO shot;
    private Picasso picasso;
    private Bitmap largeIcon;

    public SingleShotNotification(Context context, NotificationBuilderFactory builderFactory, Picasso picasso,
      ShotVO shot) {
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
        return shot.getName();
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
