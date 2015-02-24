package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;

public interface ShotQueueRepository {

    QueuedShot put(QueuedShot queuedShot);

    void remove(QueuedShot queuedShot);

}
