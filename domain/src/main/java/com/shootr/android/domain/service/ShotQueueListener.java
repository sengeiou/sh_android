package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;

public interface ShotQueueListener {

    void onSendingShot(QueuedShot shot);

    void onShotSent(QueuedShot shot);

    void onShotFailed(QueuedShot shot, Exception e);

    void onShotHasParentDeleted(QueuedShot shot, Exception e);

    void onQueueShot(QueuedShot queuedShot);

    void resetQueue();
}
