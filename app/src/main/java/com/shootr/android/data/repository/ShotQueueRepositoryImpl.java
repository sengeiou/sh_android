package com.shootr.android.data.repository;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.service.ShotQueueRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotQueueRepositoryImpl implements ShotQueueRepository {

    private List<QueuedShot> queuedShots = new ArrayList<>();
    private long idSequence = 1L;

    @Inject public ShotQueueRepositoryImpl() {
    }

    @Override public QueuedShot put(QueuedShot queuedShot) {
        queuedShot.setIdQueue(idSequence++);
        queuedShots.add(queuedShot);
        return queuedShot;
    }

    @Override public void remove(QueuedShot queuedShot) {
        queuedShots.remove(queuedShot);
    }
}
