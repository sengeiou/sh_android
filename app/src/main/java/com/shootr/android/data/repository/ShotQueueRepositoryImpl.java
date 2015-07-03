package com.shootr.android.data.repository;

import com.shootr.android.data.entity.ShotQueueEntity;
import com.shootr.android.data.mapper.ShotQueueEntityMapper;
import com.shootr.android.db.manager.ShotQueueManager;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.service.ShotQueueRepository;
import java.util.List;
import javax.inject.Inject;

public class ShotQueueRepositoryImpl implements ShotQueueRepository {

    private final ShotQueueManager shotQueueManager;
    private final ShotQueueEntityMapper mapper;

    @Inject public ShotQueueRepositoryImpl(ShotQueueManager shotQueueManager, ShotQueueEntityMapper mapper) {
        this.shotQueueManager = shotQueueManager;
        this.mapper = mapper;
    }

    @Override public QueuedShot put(QueuedShot queuedShot) {
        ShotQueueEntity savedEntity = shotQueueManager.saveShotQueue(mapper.transform(queuedShot));
        return mapper.transform(savedEntity);
    }

    @Override public void remove(QueuedShot queuedShot) {
        shotQueueManager.deleteShotQueue(mapper.transform(queuedShot));
    }

    @Override public List<QueuedShot> getPendingShotQueue() {
        return mapper.transform(shotQueueManager.retrievePendingShotQueue());
    }

    @Override public QueuedShot nextQueuedShot() {
        return mapper.transform(shotQueueManager.retrieveNextPendingShot());
    }

    @Override public List<QueuedShot> getFailedShotQueue() {
        return mapper.transform(shotQueueManager.retrieveFailedShotQueues());
    }

    @Override public QueuedShot getShotQueue(Long queuedShotId) {
        return mapper.transform(shotQueueManager.retrieveById(queuedShotId));
    }
}
