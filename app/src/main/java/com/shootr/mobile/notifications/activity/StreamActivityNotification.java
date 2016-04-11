package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.fragments.StreamTimelineFragment;
import com.shootr.mobile.util.ImageLoader;

public class StreamActivityNotification extends SingleActivityNotification {

    private static final int REQUEST_OPEN = 2;
    private final String idStream;
    private final String idStreamHolder;
    private final String shortTitle;

    public StreamActivityNotification(Context context, NotificationBuilderFactory notificationBuilderFactory,
      ImageLoader imageLoader, PushNotification.NotificationValues notificationValues, String idStream,
      String idStreamHolder, String shortTitle) {
        super(context, notificationBuilderFactory, imageLoader, notificationValues);
        this.idStream = idStream;
        this.idStreamHolder = idStreamHolder;
        this.shortTitle = shortTitle;
    }

    @Override public void setNotificationValues(NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentIntent(getOpenStreamTimelineNotificationPendingIntent());
    }

    private PendingIntent getOpenStreamTimelineNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_STREAM);
        intent.putExtra(StreamTimelineFragment.EXTRA_ID_USER, idStreamHolder);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_ID, idStream);
        intent.putExtra(StreamTimelineFragment.EXTRA_STREAM_SHORT_TITLE, shortTitle);
        return PendingIntent.getBroadcast(getContext(), REQUEST_OPEN, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
