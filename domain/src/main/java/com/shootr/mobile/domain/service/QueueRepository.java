package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.model.shot.QueuedShot;
import java.util.List;

public interface QueueRepository {

    String SHOT_TYPE = "shot";
    String MESSAGE_TYPE = "message";

    QueuedShot put(QueuedShot queuedShot);

    void remove(QueuedShot queuedShot);

    List<QueuedShot> getPendingQueue();

    QueuedShot nextQueued(String queuedType);

    List<QueuedShot> getFailedQueue();

    QueuedShot getQueue(Long queuedShotId, String queuedType);
}
