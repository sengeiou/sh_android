package gm.mobi.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;

public class FollowerNotification extends CommonNotification {

    private User user;

    public FollowerNotification(Context context, NotificationBuilderFactory builderFactory, User user) {
        super(context, builderFactory);
        this.user = user;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(getResources().getString(R.string.notification_follow_title, user.getName()));
        String warningMessage = "Notificación no definida aún en ninguna tarea. No la termino hasta que esté definida.";
        builder.setContentText(warningMessage);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(warningMessage));
    }

    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }
}
