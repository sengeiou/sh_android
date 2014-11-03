package gm.mobi.android.gcm.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import gm.mobi.android.R;
import gm.mobi.android.gcm.NotificationIntentReceiver;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.model.UserModel;

public class FollowerNotification extends CommonNotification {

    private UserModel user;
    private static final int REQUEST_OPEN = 2;
    public FollowerNotification(Context context, NotificationBuilderFactory builderFactory, UserModel user) {
        super(context, builderFactory);
        this.user = user;
    }

    @Override
    public void setNotificationValues(NotificationCompat.Builder builder) {
        builder.setContentTitle(getResources().getString(R.string.notification_follow_title, user.getUserName()));
        String message = getResources().getString(R.string.following_notif_text);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentIntent(getOpenProfileNotificationPendingIntent());
    }

    @Override
    public Bitmap getLargeIcon() {
        return null;
    }

    @Override
    public Bitmap getWearBackground() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.drawer_background);
    }
    protected PendingIntent getOpenProfileNotificationPendingIntent() {

        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN,
         ProfileContainerActivity.getIntent(getContext(),user.getIdUser()), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //protected PendingIntent getDiscardShotNotificationPendingIntent() {
    //    return PendingIntent.getBroadcast(getContext(), REQUEST_DELETE,
    //      new Intent(NotificationIntentReceiver.ACTION_DISCARD_SHOT_NOTIFICATION), 0);
    //}
}


