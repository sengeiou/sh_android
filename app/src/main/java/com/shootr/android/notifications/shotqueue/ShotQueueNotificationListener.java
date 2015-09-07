package com.shootr.android.notifications.shotqueue;

import android.os.Handler;
import android.os.Looper;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.service.ShotQueueListener;
import com.shootr.android.notifications.AndroidNotificationManager;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotQueueNotificationListener implements ShotQueueListener {

    private static final int SHOT_SENT_HIDE_DELAY_MILLIS = 2000;

    private final ShotQueueNotificationManager shotQueueNotificationManager;
    private final AndroidNotificationManager androidNotificationManager;

    @Inject public ShotQueueNotificationListener(ShotQueueNotificationManager shotQueueNotificationManager,
      AndroidNotificationManager androidNotificationManager) {
        this.shotQueueNotificationManager = shotQueueNotificationManager;
        this.androidNotificationManager = androidNotificationManager;
    }

    @Override public void onSendingShot(QueuedShot shot) {
        /* no-op */
    }

    @Override public void onShotSent(final QueuedShot shot) {
        shotQueueNotificationManager.showShotSentNotification(shot);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                shotQueueNotificationManager.hideShotNotification(shot);
            }
        }, SHOT_SENT_HIDE_DELAY_MILLIS);
    }

    @Override public void onShotFailed(QueuedShot shot, Exception e) {
        shotQueueNotificationManager.showShotFailedNotification(shot);
        Timber.e(e, "Shot failed");
    }

    @Override public void onQueueShot(QueuedShot queuedShot) {
        shotQueueNotificationManager.showSendingShotNotification(queuedShot);
    }

    @Override public void resetQueue() {
        androidNotificationManager.cancelAll();
    }
}
