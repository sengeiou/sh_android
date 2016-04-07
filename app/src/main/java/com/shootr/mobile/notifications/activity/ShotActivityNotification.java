package com.shootr.mobile.notifications.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.notifications.NotificationBuilderFactory;
import com.shootr.mobile.notifications.gcm.NotificationIntentReceiver;
import com.shootr.mobile.notifications.gcm.PushNotification;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.util.ImageLoader;

public class ShotActivityNotification extends SingleActivityNotification{

    private static final int REQUEST_OPEN = 2;
    private final ShotRepository remoteShotRepository;
    private final ShotModelMapper shotModelMapper;
    private final String idShot;

    public ShotActivityNotification(Context context, NotificationBuilderFactory builderFactory, ImageLoader imageLoader,
      PushNotification.NotificationValues values, String idShot,
      ShotRepository remoteShotRepository, ShotModelMapper shotModelMapper) {
        super(context, builderFactory, imageLoader, values);
        this.idShot = idShot;
        this.remoteShotRepository = remoteShotRepository;
        this.shotModelMapper = shotModelMapper;
    }

    @Override public void setNotificationValues(final NotificationCompat.Builder builder) {
        super.setNotificationValues(builder);
        builder.setContentIntent(getOpenShotDetailNotificationPendingIntent());
    }

    private PendingIntent getOpenShotDetailNotificationPendingIntent() {
        Intent intent = new Intent(NotificationIntentReceiver.ACTION_OPEN_SHOT_DETAIL);
        Shot shot = remoteShotRepository.getShot(idShot);
        intent.putExtra(ShotDetailActivity.EXTRA_SHOT, shotModelMapper.transform(shot));
        return PendingIntent.getBroadcast(getContext(),REQUEST_OPEN,intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
