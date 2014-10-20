package gm.mobi.android.gcm.notifications;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.SparseArray;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.gcm.NotificationIntentReceiver;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.model.ShotVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class BagdadNotificationManager {

    public static final int NOTIFICATION_ERROR = 0;
    public static final int NOTIFICATION_SHOT = 1;
    private static final int NOTIFICATION_FOLLOW = 2;

    private Context context;
    private NotificationManagerCompat notificationManager;
    private NotificationBuilderFactory notificationBuilderFactory;
    private Picasso picasso;

    private List<ShotVO> shotsCurrentlyNotified = new ArrayList<>();

    @Inject public BagdadNotificationManager(Application context, NotificationManagerCompat notificationManager,
      NotificationBuilderFactory notificationBuilderFactory, Picasso picasso) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.notificationBuilderFactory = notificationBuilderFactory;
        this.picasso = picasso;
    }

    public void sendNewShotNotification(ShotVO shot) {
        //TODO check if the timeline is currently shown
        shotsCurrentlyNotified.add(shot);

        Notification notification;
        if (shotsCurrentlyNotified.size() > 1) {
            notification = buildMultipleShotNotification(shotsCurrentlyNotified);
        } else {
            notification = buildSingleShotNotification(shot);
        }
        notify(NOTIFICATION_SHOT, notification);
    }

    protected Notification buildSingleShotNotification(ShotVO shot) {
        return new SingleShotNotification(context, notificationBuilderFactory, picasso, shot).build();
    }

    protected Notification buildMultipleShotNotification(List<ShotVO> shots) {
        return new MultipleShotNotification(context, notificationBuilderFactory, shots).build();
    }


    public void sendErrorNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("Error")
          .setContentText(message)
          .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notify(NOTIFICATION_ERROR, builder.build());
    }

    public void clearShotNotifications() {
        notificationManager.cancel(NOTIFICATION_SHOT);
        shotsCurrentlyNotified.clear();
    }

    private void notify(int id, Notification notification) {
        if(areNotificationsEnabled()){
            notificationManager.notify(id, notification);
        }
    }

    public boolean areNotificationsEnabled() {
        return true;
    }

    public void sendNewFollowerNotification(User user) {
        Notification notification = new FollowerNotification(context, notificationBuilderFactory, user).build();
        //TODO ids generados
        notify(NOTIFICATION_FOLLOW, notification);
    }
}
