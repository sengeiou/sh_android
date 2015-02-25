package com.shootr.android.notifications.shotqueue;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.service.ShotQueueListener;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotQueueNotificationListener implements ShotQueueListener {

    private final ShotQueueNotificationManager shotQueueNotificationManager;

    @Inject public ShotQueueNotificationListener(ShotQueueNotificationManager shotQueueNotificationManager) {
        this.shotQueueNotificationManager = shotQueueNotificationManager;
    }

    @Override public void onSendingShot(QueuedShot shot) {
    }

    @Override public void onShotSent(QueuedShot shot) {
        shotQueueNotificationManager.hideSendingShotNotification(shot);
    }

    @Override public void onShotFailed(QueuedShot shot) {
        shotQueueNotificationManager.hideSendingShotNotification(shot);
        Timber.e("Shot failed");
    }

    @Override public void onQueueShot(QueuedShot queuedShot) {
        shotQueueNotificationManager.showSendingShotNotification(queuedShot);
    }
}
