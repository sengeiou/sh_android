package com.shootr.mobile.data.repository;

import com.shootr.mobile.data.entity.PrivateMessageQueueEntity;
import com.shootr.mobile.data.entity.ShotQueueEntity;
import com.shootr.mobile.data.mapper.PrivateMessageQueueEntityMapper;
import com.shootr.mobile.data.mapper.ShotQueueEntityMapper;
import com.shootr.mobile.db.manager.MessageQueueManager;
import com.shootr.mobile.db.manager.ShotQueueManager;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.service.QueueRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class QueueRepositoryImpl implements QueueRepository {

  private final ShotQueueManager shotQueueManager;
  private final ShotQueueEntityMapper mapper;
  private final PrivateMessageQueueEntityMapper privateMessageMapper;
  private final MessageQueueManager privateQueueManager;

  @Inject
  public QueueRepositoryImpl(ShotQueueManager shotQueueManager, ShotQueueEntityMapper mapper,
      PrivateMessageQueueEntityMapper privateMessageMapper,
      MessageQueueManager privateQueueManager) {
    this.shotQueueManager = shotQueueManager;
    this.mapper = mapper;
    this.privateMessageMapper = privateMessageMapper;
    this.privateQueueManager = privateQueueManager;
  }

  @Override public QueuedShot put(QueuedShot queuedShot) {
    if (queuedShot.getBaseMessage() instanceof Shot) {
      ShotQueueEntity savedEntity =
          shotQueueManager.saveShotQueue(mapper.transformShotQueue(queuedShot));
      return mapper.transformShotQueue(savedEntity);
    } else {
      PrivateMessageQueueEntity entity = privateQueueManager.saveMessageQueue(
          privateMessageMapper.transformPrivateMessageQueue(queuedShot));
      return privateMessageMapper.transformPrivateMessageQueue(entity);
    }
  }

  @Override public void remove(QueuedShot queuedShot) {
    if (queuedShot != null) {
      if (queuedShot.getBaseMessage() instanceof Shot) {
        shotQueueManager.deleteShotQueue(mapper.transformShotQueue(queuedShot));
      } else {
        privateQueueManager.deleteMessageQueue(
            privateMessageMapper.transformPrivateMessageQueue(queuedShot));
      }
    }
  }

  @Override public void remove(String idQueuedShot) {
    shotQueueManager.deleteShotQueue(idQueuedShot);
  }

  @Override public List<QueuedShot> getPendingQueue() {
    ArrayList<QueuedShot> queued = new ArrayList<>();
    queued.addAll(mapper.transformShotQueue(shotQueueManager.retrievePendingShotQueue()));
    queued.addAll(privateMessageMapper.transformPrivateMessageQueue(
        privateQueueManager.retrievePendingMessageQueue()));
    return queued;
  }

  @Override public QueuedShot nextQueued(String queuedType) {
    if (queuedType.equals(SHOT_TYPE)) {
      return mapper.transformShotQueue(shotQueueManager.retrieveNextPendingShot());
    } else {
      return privateMessageMapper.transformPrivateMessageQueue(
          privateQueueManager.retrieveNextPendingMessage());
    }
  }

  @Override public List<QueuedShot> getFailedQueue() {
    ArrayList<QueuedShot> queued = new ArrayList<>();
    queued.addAll(mapper.transformShotQueue(shotQueueManager.retrieveFailedShotQueues()));
    queued.addAll(privateMessageMapper.transformPrivateMessageQueue(
        privateQueueManager.retrieveFailedMessageQueues()));
    return queued;
  }

  @Override public QueuedShot getQueue(Long queuedShotId, String queuedType) {
    if (queuedType.equals(SHOT_TYPE)) {
      return mapper.transformShotQueue(shotQueueManager.retrieveById(queuedShotId));
    } else {
      return privateMessageMapper.transformPrivateMessageQueue(
          privateQueueManager.retrieveById(queuedShotId));
    }
  }
}
