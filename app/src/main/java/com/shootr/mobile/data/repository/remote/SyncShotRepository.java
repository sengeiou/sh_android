package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.HighlightedShotEntityMapper;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncShotRepository implements ExternalShotRepository, SyncableRepository {

  private final ShotDataSource remoteShotDataSource;
  private final ShotDataSource localShotDataSource;
  private final ShotEntityMapper shotEntityMapper;
  private final HighlightedShotEntityMapper highlightedShotEntityMapper;
  private final UserDataSource userDataSource;
  private final SyncTrigger syncTrigger;

  @Inject public SyncShotRepository(@Remote ShotDataSource remoteShotDataSource,
      @Local ShotDataSource localShotDataSource, ShotEntityMapper shotEntityMapper,
      HighlightedShotEntityMapper highlightedShotEntityMapper, @Local UserDataSource userDataSource,
      SyncTrigger syncTrigger) {
    this.remoteShotDataSource = remoteShotDataSource;
    this.localShotDataSource = localShotDataSource;
    this.shotEntityMapper = shotEntityMapper;
    this.highlightedShotEntityMapper = highlightedShotEntityMapper;
    this.userDataSource = userDataSource;
    this.syncTrigger = syncTrigger;
  }

  @Override public Shot putShot(Shot shot) {
    ShotEntity shotEntity = shotEntityMapper.transform(shot);
    ShotEntity responseShotEntity = remoteShotDataSource.putShot(shotEntity);
    UserEntity userEntity = userDataSource.getUser(responseShotEntity.getIdUser());
    responseShotEntity.setUsername(userEntity.getUserName());
    responseShotEntity.setUserPhoto(userEntity.getPhoto());
    localShotDataSource.putShot(responseShotEntity);
    return shotEntityMapper.transform(responseShotEntity);
  }

  @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
    try {
      List<ShotEntity> shotEntitiesFromTimeline =
          remoteShotDataSource.getShotsForStreamTimeline(parameters);
      localShotDataSource.putShots(shotEntitiesFromTimeline);
      return shotEntityMapper.transform(shotEntitiesFromTimeline);
    } catch (ServerCommunicationException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  @Override public Shot getShot(String shotId, String[] streamTypes, String[] shotTypes) {
    ShotEntity shot = localShotDataSource.getShot(shotId, streamTypes, shotTypes);
    if (shot == null) {
      try {
        shot = remoteShotDataSource.getShot(shotId, streamTypes, shotTypes);
      } catch (ShotNotFoundException exception) {
        throw exception;
      }
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
    if (shotDetail.getReplies() != null) {
      localShotDataSource.putShots(shotDetail.getReplies());
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

  @Override public void highlightShot(String idShot) {
    HighlightedShotEntity highlightedShotEntity = remoteShotDataSource.highlightShot(idShot);
    localShotDataSource.putHighlightShot(highlightedShotEntity);
  }

  @Override public void callCtaCheckIn(String idStream) throws UserAlreadyCheckInRequestException,
      UserCannotCheckInRequestException {
    remoteShotDataSource.callCtaCheckIn(idStream);
  }

  @Override public HighlightedShot getHighlightedShots(String idStream) {
    return highlightedShotEntityMapper.mapToDomain(
        remoteShotDataSource.getHighlightedShot(idStream));
  }

  @Override public void dismissHighlightedShot(String idHighlightedShot) {
    remoteShotDataSource.dismissHighlight(idHighlightedShot);
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
