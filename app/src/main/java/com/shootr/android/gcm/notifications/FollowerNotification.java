package com.shootr.android.gcm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.shootr.android.R;
import com.shootr.android.ui.model.UserModel;

public class FollowerNotification extends CommonNotification {

    private UserModel user;

    public FollowerNotification(Context context, NotificationBuilderFactory builderFactory, UserModel user) {
        super(context, builderFactory);
        this.user = user;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(getResources().getString(R.string.notification_follow_title, user.getUserName()));
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
