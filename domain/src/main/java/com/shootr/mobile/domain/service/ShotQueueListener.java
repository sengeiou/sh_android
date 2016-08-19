package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.model.shot.QueuedShot;

public interface ShotQueueListener {

    void onSendingShot(QueuedShot shot);

    void onShotSent(QueuedShot shot);

    void onShotFailed(QueuedShot shot, Exception e);

    void onShotHasParentDeleted(QueuedShot shot, Exception e);

    void onShotHasStreamRemoved(QueuedShot shot, Exception e);

    void onQueueShot(QueuedShot queuedShot);

    void resetQueue();

    void onShotIsOnReadOnly(QueuedShot queuedShot, Exception e);
}
