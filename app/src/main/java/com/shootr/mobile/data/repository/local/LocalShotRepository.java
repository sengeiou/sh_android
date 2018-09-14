package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.mapper.HighlightedShotEntityMapper;
import com.shootr.mobile.data.mapper.ProfileShotTimelineMapper;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.remote.cache.ShotDetailCache;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.shot.NewShotDetail;
import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalShotRepository implements InternalShotRepository {

  private final ShotDataSource localShotDataSource;
  private final ShotEntityMapper shotEntityMapper;
  private final HighlightedShotEntityMapper highlightedShotEntityMapper;
  private final ProfileShotTimelineMapper profileShotTimelineMapper;
  private final SessionRepository sessionRepository;
  private final ShotDetailCache shotDetailCache;

  @Inject public LocalShotRepository(@Local ShotDataSource localShotDataSource,
      ShotEntityMapper shotEntityMapper, HighlightedShotEntityMapper highlightedShotEntityMapper,
      ProfileShotTimelineMapper profileShotTimelineMapper, SessionRepository sessionRepository,
      ShotDetailCache shotDetailCache) {
    this.localShotDataSource = localShotDataSource;
    this.shotEntityMapper = shotEntityMapper;
    this.highlightedShotEntityMapper = highlightedShotEntityMapper;
    this.profileShotTimelineMapper = profileShotTimelineMapper;
    this.sessionRepository = sessionRepository;
    this.shotDetailCache = shotDetailCache;
  }

  @Override public Shot putShot(Shot shot) {
    localShotDataSource.putShot(shotEntityMapper.transform(shot),
        sessionRepository.getCurrentUserId());
    return shot;
  }

  @Override public List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
    List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForStreamTimeline(parameters);
    return shotEntityMapper.transform(shotsForEvent);
  }

  @Override public List<Shot> getShotsForStreamTimelineFiltered(StreamTimelineParameters parameters) {
    List<ShotEntity> shotsForEvent = localShotDataSource.getShotsForStreamTimelineFiltered(parameters);
    return shotEntityMapper.transform(shotsForEvent);
  }

  @Override public Shot getShot(String shotId, String[] streamTypes, String[] shotTypes) {
    ShotEntity shot = localShotDataSource.getShot(shotId, streamTypes, shotTypes);
    return shotEntityMapper.transform(shot);
  }

  @Override
  public List<Shot> getMediaByIdStream(String idEvent, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes) {
    List<ShotEntity> shotEntitiesWithMedia =
        localShotDataSource.getStreamMediaShots(idEvent, maxTimestamp, streamTypes,
            shotTypes);
    return shotEntityMapper.transform(shotEntitiesWithMedia);
  }

  @Override public List<Shot> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes) {
    return shotEntityMapper.transform(
        localShotDataSource.getShotsFromUser(idUser, limit, streamTypes, shotTypes));
  }

  @Override
  public List<Shot> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes) {
    return shotEntityMapper.transform(
        localShotDataSource.getAllShotsFromUser(userId, streamTypes, shotTypes));
  }

  @Override public List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes) {
    return shotEntityMapper.transform(
        localShotDataSource.getAllShotsFromUserAndDate(userId, currentOldestDate, streamTypes,
            shotTypes));
  }

  @Override public void putShots(List<Shot> shotsFromUser) {
    localShotDataSource.putShots(shotEntityMapper.transformInEntities(shotsFromUser),
        sessionRepository.getCurrentUserId());
  }

  @Override public void deleteShot(String idShot) {
    localShotDataSource.deleteShot(idShot);
  }

  @Override
  public List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters) {
    List<ShotEntity> shotsForStream =
        localShotDataSource.getUserShotsForStreamTimeline(timelineParameters);
    return shotEntityMapper.transform(shotsForStream);
  }

  @Override public void deleteShotsByStream(String idStream) {
    localShotDataSource.deleteShotsByIdStream(idStream);
  }

  @Override
  public void hideShot(String idShot, Long timestamp, String[] streamTypes, String[] shotTypes) {
    localShotDataSource.hideShot(idShot, timestamp);
  }

  @Override public HighlightedShot getHighlightedShots(String idStream) {
    return highlightedShotEntityMapper.mapToDomain(
        localShotDataSource.getHighlightedShot(idStream));
  }

  @Override public void dismissHighlightedShot(String idHighlightedShot) {
    localShotDataSource.dismissHighlight(idHighlightedShot);
  }

  @Override public void reshoot(String idShot) {
    localShotDataSource.reshoot(idShot);
  }

  @Override public void undoReshoot(String idShot) {
    localShotDataSource.undoReshoot(idShot);
  }

  @Override
  public ProfileShotTimeline getProfileShotTimeline(String idUser, Long maxTimestamp, int count) {
    return profileShotTimelineMapper.map(
        localShotDataSource.getProfileShotTimeline(idUser, maxTimestamp, count));
  }

  @Override public void hideHighlightedShot(String idHighlightedShot) {
    localShotDataSource.hideHighlightedShot(idHighlightedShot);
  }

  @Override public boolean hasNewFilteredShots(String idStream, String lastTimeFiltered) {
    return localShotDataSource.hasNewFilteredShots(idStream, lastTimeFiltered, sessionRepository.getCurrentUserId());
  }

  @Override public NewShotDetail getCachedShotDetail(String idShot) {
    return shotDetailCache.getShot(idShot);
  }
}
