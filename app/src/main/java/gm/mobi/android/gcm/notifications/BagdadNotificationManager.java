package gm.mobi.android.gcm.notifications;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class BagdadNotificationManager {

    public static final int NOTIFICATION_SHOT = 1;
    public static final int NOTIFICATION_ERROR = 0;

    private static final int REQUEST_DELETE = 1;
    private static final int REQUEST_OPEN = 2;

    private Context context;
    private NotificationManagerCompat notificationManager;
    private Picasso picasso;

    private SparseArray<Shot> shotsCurrentlyNotified = new SparseArray<>();

    @Inject public BagdadNotificationManager(Context context, NotificationManagerCompat notificationManager,
      Picasso picasso) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.picasso = picasso;
    }

    public void sendNewShotNotification(Shot shot) {
        //TODO check if the timeline is currently shown
        shotsCurrentlyNotified.append(shot.getIdShot().intValue(), shot);

        Notification notification;
        if (shotsCurrentlyNotified.size() > 1) {
            // Update the current notification
            notification = buildMultipleShotNotification(shotsCurrentlyNotified);
        } else {
            // Single notification
            notification = buildSingleShotNotification(shot);
        }
        notificationManager.notify(NOTIFICATION_SHOT, notification);
    }

    protected Notification buildSingleShotNotification(Shot shot) {
        User user = shot.getUser();
        Bitmap avatar = getUserPhoto(user.getPhoto());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle(user.getName())
          .setContentText(shot.getComment())
          .setLargeIcon(avatar)
          .setStyle(new NotificationCompat.BigTextStyle().bigText(shot.getComment()));
        builder = setShotNotificationCommonValues(builder);
        return builder.build();
    }

    protected Notification buildMultipleShotNotification(SparseArray<Shot> shots) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        int shotCount = shots.size();
        List<String> userNames = getUserNamesFromShots(shots);

        String multipleShotsTilte = context.getString(R.string.notification_shot_multiple_title, shotCount);
        builder.setContentTitle(multipleShotsTilte);

        String collapsedContentText = TextUtils.join(",", userNames);
        builder.setContentText(collapsedContentText);

        builder.setStyle(getInboxStyleFromShots(shots, builder));

        builder = setShotNotificationCommonValues(builder);

        NotificationCompat.WearableExtender wearableExtender =
          new NotificationCompat.WearableExtender().setBackground(getWearableBackground());

        builder.extend(wearableExtender);

        return builder.build();
    }

    protected NotificationCompat.Builder setShotNotificationCommonValues(NotificationCompat.Builder builder) {
        return builder.setSmallIcon(R.drawable.ic_ab_icon)
          .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
          .setAutoCancel(true)
          .setContentIntent(getOpenShotNotificationPendingIntent())
          .setDeleteIntent(getDiscardShotNotificationPendingIntent());
    }

    protected PendingIntent getDiscardShotNotificationPendingIntent() {
        return PendingIntent.getBroadcast(context, REQUEST_DELETE,
          new Intent(NotificationIntentReceiver.ACTION_DISCARD_SHOT_NOTIFICATION), 0);
    }

    protected PendingIntent getOpenShotNotificationPendingIntent() {
        Bundle extras = new Bundle();
        extras.putInt(MainActivity.EXTRA_SHOW_SECTION,
          MainActivity.DRAWER_POSITION_TIMELINE);

        return PendingIntent.getBroadcast(context, REQUEST_OPEN,
          new Intent(NotificationIntentReceiver.ACTION_OPEN_SHOT_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT);
          //new Intent(context, MainActivity.class).putExtras(extras), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    protected Bitmap getUserPhoto(String url) {
        try {
            return picasso.load(url).get();
        } catch (IOException | IllegalArgumentException e) {
            Timber.e(e, "Error downloading user photo for a notification.");
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_picture_default);
        }
    }

    protected List<String> getUserNamesFromShots(SparseArray<Shot> shots) {
        List<String> names = new ArrayList<>(shots.size());
        for (int i = 0; i < shots.size(); i++) {
            Shot shot = shots.valueAt(i);
            String userName = shot.getUser().getName();
            names.add(userName);
        }
        Collections.sort(names);
        return names;
    }

    protected NotificationCompat.InboxStyle getInboxStyleFromShots(SparseArray<Shot> shots,
      NotificationCompat.Builder builder) {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle(builder);
        for (int i = 0; i < shots.size(); i++) {
            Shot shot = shots.valueAt(i);
            User user = shot.getUser();
            String userName = user.getName();
            String shotText = shot.getComment();
            Spannable styledLine = getSpannableLineFromNameAndComment(userName, shotText);
            inbox.addLine(styledLine);
        }
        return inbox;
    }

    protected Spannable getSpannableLineFromNameAndComment(String name, String comment) {
        Spannable styledLine = new SpannableString(name + " " + comment);
        int nameEndIndex = name.length();
        styledLine.setSpan(new StyleSpan(Typeface.BOLD), 0, nameEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledLine;
    }

    protected Bitmap getWearableBackground() {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.drawer_background);
    }

    public void clearShotNotifications() {
        notificationManager.cancel(NOTIFICATION_SHOT);
        shotsCurrentlyNotified.clear();
    }

    public void sendErrorNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("Error")
          .setContentText(message)
          .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notificationManager.notify(NOTIFICATION_ERROR, builder.build());
    }
}
