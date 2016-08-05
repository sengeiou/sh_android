package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.model.shot.QueuedShot;
import java.util.List;

public interface ShotQueueRepository {

    QueuedShot put(QueuedShot queuedShot);

    void remove(QueuedShot queuedShot);

    List<QueuedShot> getPendingShotQueue();

    QueuedShot nextQueuedShot();

    List<QueuedShot> getFailedShotQueue();

    QueuedShot getShotQueue(Long queuedShotId);
}
