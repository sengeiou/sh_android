package com.shootr.android.notifications;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.service.ShotQueueListener;
import javax.inject.Inject;

public class ShotQueueNotificationListener implements ShotQueueListener {

    @Inject public ShotQueueNotificationListener() {
    }

    @Override public void onSendingShot(QueuedShot shot) {

    }

    @Override public void onShotSent(QueuedShot shot) {

    }

    @Override public void onShotFailed(QueuedShot shot) {

    }
}
