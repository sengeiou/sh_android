package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.model.shot.QueuedShot;

public interface ShotQueueListener {

    void onSendingShot(QueuedShot shot);

    void onShotSent(QueuedShot shot);

    void onShotFailed(QueuedShot shot, Exception e);

    void onShotHasParentDeleted(QueuedShot shot, Exception e);

    void onPrivateMessageNotAllowed(QueuedShot shot, Exception e);

    void onPrivateMessageBlockedUser(QueuedShot shot, Exception e);

    void onShotHasStreamRemoved(QueuedShot shot, Exception e);

    void onQueueShot(QueuedShot queuedShot);

    void resetQueue();

    void onShotIsOnReadOnly(QueuedShot queuedShot, Exception e);

    void onShotHasParentDeleted(QueuedShot shot);

    void onShotHasStreamRemoved(QueuedShot shot);

    void onShotIsOnReadOnly(QueuedShot queuedShot);
}
