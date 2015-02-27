package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;
import java.util.List;

public interface ShotQueueRepository {

    QueuedShot put(QueuedShot queuedShot);

    void remove(QueuedShot queuedShot);

    List<QueuedShot> getPendingShotQueue();

    QueuedShot nextQueuedShot();
}
