package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncShotRepository implements ShotRepository, SyncableRepository {

  private final ShotDataSource remoteShotDataSource;
  private final ShotDataSource localShotDataSource;
  private final ShotEntityMapper shotEntityMapper;
  private final SyncTrigger syncTrigger;

  @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource,
      @Local ShotDataSource localShotDataSource, ShotEntityMapper shotEntityMapper,
      SyncTrigger syncTrigger) {
    this.remoteShotDataSource = remoteShotDataSource;
    this.localShotDataSource = localShotDataSource;
    this.shotEntityMapper = shotEntityMapper;
    this.syncTrigger = syncTrigger;
  }

  @Override public Shot putShot(Shot shot) {
    ShotEntity shotEntity = shotEntityMapper.transform(shot);
    ShotEntity responseShotEntity = remoteShotDataSource.putShot(shotEntity);
    return shotEntityMapper.transform(responseShotEntity);
  }

  @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
    List<ShotEntity> shotEntitiesFromTimeline =
        remoteShotDataSource.getShotsForStreamTimeline(parameters);
    localShotDataSource.putShots(shotEntitiesFromTimeline);
    return shotEntityMapper.transform(shotEntitiesFromTimeline);
  }

  @Override public Shot getShot(String shotId, String[] streamTypes, String[] shotTypes) {
    ShotEntity shot = localShotDataSource.getShot(shotId, streamTypes, shotTypes);
    if (shot == null) {
      shot = remoteShotDataSource.getShot(shotId, streamTypes, shotTypes);
    }
    return shotEntityMapper.transform(shot);
  }

  @Override public List<Shot> getReplies(String shot, String[] streamTypes, String[] shotTypes) {
    return shotEntityMapper.transform(
        remoteShotDataSource.getReplies(shot, streamTypes, shotTypes));
  }

  @Override
  public List<Shot> getMediaByIdStream(String idEvent, List<String> userId, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes) {
    List<ShotEntity> shotEntitiesWithMedia =
        remoteShotDataSource.getStreamMediaShots(idEvent, userId, maxTimestamp, streamTypes,
            shotTypes);
    return shotEntityMapper.transform(shotEntitiesWithMedia);
  }

  @Override public List<Shot> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes) {
    syncTrigger.triggerSync();
    return shotEntityMapper.transform(
        remoteShotDataSource.getShotsFromUser(idUser, limit, streamTypes, shotTypes));
  }

  @Override
  public ShotDetail getShotDetail(String idShot, String[] streamTypes, String[] shotTypes) {
    ShotDetailEntity shotDetail =
        remoteShotDataSource.getShotDetail(idShot, streamTypes, shotTypes);
    if (shotDetail.getParents() != null) {
      localShotDataSource.putShots(shotDetail.getParents());
    }
    return shotEntityMapper.transform(shotDetail);
  }

  @Override
  public List<Shot> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes) {
    return shotEntityMapper.transform(
        remoteShotDataSource.getAllShotsFromUser(userId, streamTypes, shotTypes));
  }

  @Override public List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes) {
    return shotEntityMapper.transform(
        remoteShotDataSource.getAllShotsFromUserAndDate(userId, currentOldestDate, streamTypes,
            shotTypes));
  }

  @Override public void putShots(List<Shot> shotsFromUser) {
    throw new IllegalArgumentException("putShots not implemented in remote");
  }

  @Override public void shareShot(String idShot) {
    remoteShotDataSource.shareShot(idShot);
  }

  @Override public void deleteShot(String idShot) {
    remoteShotDataSource.deleteShot(idShot);
  }

  @Override
  public List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
    List<ShotEntity> shotEntitiesFromTimeline =
        remoteShotDataSource.getUserShotsForStreamTimeline(timelineParameters);
    localShotDataSource.putShots(shotEntitiesFromTimeline);
    return shotEntityMapper.transform(shotEntitiesFromTimeline);
  }

  @Override public void deleteShotsByStream(String idStream) {
    throw new IllegalArgumentException("deleteShotsByStream not implemented in remote");
  }

  @Override
  public void hideShot(String idShot, Long timestamp, String[] streamTypes, String[] shotTypes) {
    ShotEntity shotEntity = getShotEntity(idShot, streamTypes, shotTypes);
    try {
      remoteShotDataSource.hideShot(idShot, timestamp);
      shotEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
      shotEntity.setProfileHidden(timestamp);
      localShotDataSource.putShot(shotEntity);
    } catch (ServerCommunicationException e) {
      shotEntity.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
      localShotDataSource.hideShot(idShot, timestamp);
      localShotDataSource.putShot(shotEntity);
      syncTrigger.notifyNeedsSync(this);
    }
  }

  private ShotEntity getShotEntity(String idShot, String[] streamTypes, String[] shotTypes) {
    ShotEntity shot = localShotDataSource.getShot(idShot, streamTypes, shotTypes);
    if (shot == null) {
      shot = remoteShotDataSource.getShot(idShot, streamTypes, shotTypes);
    }
    return shot;
  }

  @Override public void unhideShot(String idShot) {
    remoteShotDataSource.unhideShot(idShot);
  }

  @Override public void dispatchSync() {
    List<ShotEntity> pendingEntities = localShotDataSource.getEntitiesNotSynchronized();
    for (ShotEntity entity : pendingEntities) {
      syncHiddenEntities(entity);
    }
  }

  private void syncHiddenEntities(ShotEntity entity) {
    try {
      remoteShotDataSource.hideShot(entity.getIdShot(), entity.getProfileHidden());
      entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
      localShotDataSource.hideShot(entity.getIdShot(), entity.getProfileHidden());
    } catch (Exception error) {
      error.printStackTrace();
    }
  }
}
