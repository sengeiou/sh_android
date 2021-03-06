package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ProfileShotTimelineEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.db.manager.HighlightedShotManager;
import com.shootr.mobile.db.manager.ShotManager;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabaseShotDataSource implements ShotDataSource {

  private final ShotManager shotManager;
  private final HighlightedShotManager highlightedManager;

  @Inject public DatabaseShotDataSource(ShotManager shotManager,
      HighlightedShotManager highlightedManager) {
    this.shotManager = shotManager;
    this.highlightedManager = highlightedManager;
  }

  @Override public ShotEntity putShot(ShotEntity shotEntity, String idUserMe) {
    shotManager.saveShot(shotEntity, idUserMe);
    return shotEntity;
  }

  @Override public void putShotViaSocket(ShotEntity shotEntity, String idQueue) {
    throw new IllegalArgumentException("reshoot should not have local implementation");
  }

  @Override public void putShots(List<ShotEntity> shotEntities, String idUserMe) {
    shotManager.saveShots(shotEntities, idUserMe);
  }

  @Override public List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters) {
    return shotManager.getShotsByStreamParameters(parameters);
  }

  @Override public List<ShotEntity> getShotsForStreamTimelineFiltered(StreamTimelineParameters parameters) {
    return shotManager.getShotsByStreamParametersFiltered(parameters);
  }

  @Override public ShotEntity getShot(String shotId, String[] streamTypes, String[] shotTypes) {
    return shotManager.getShotById(shotId);
  }

  @Override public List<ShotEntity> getStreamMediaShots(String idStream,
      Long maxTimestamp, String[] streamTypes, String[] shotTypes) {
    return shotManager.getStreamMediaShots(idStream);
  }

  @Override
  public List<ShotEntity> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes) {
    return shotManager.getShotsFromUser(idUser, limit);
  }

  @Override public List<ShotEntity> getAllShotsFromUser(String userId, String[] streamTypes,
      String[] shotTypes) {
    return shotManager.getAllShotsFromUser(userId);
  }

  @Override
  public List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes) {
    throw new IllegalArgumentException(
        "getAllShotsFromUserWithMaxDate should have no local implementation");
  }

  @Override public void reshoot(String idShot) {
    shotManager.reshoot(idShot);
  }

  @Override public void undoReshoot(String idShot) {
    shotManager.undoReshoot(idShot);
  }

  @Override public void deleteShot(String idShot) {
    shotManager.deleteShot(idShot);
  }

  @Override public List<ShotEntity> getUserShotsForStreamTimeline(
      StreamTimelineParameters timelineParameters) {
    return shotManager.getUserShotsByParameters(timelineParameters);
  }

  @Override public List<ShotEntity> updateImportantShots(StreamTimelineParameters parameters) {
    throw new IllegalArgumentException("reshoot should not have local implementation");
  }

  @Override public void deleteShotsByIdStream(String idStream) {
    shotManager.deleteShotsByIdStream(idStream);
  }

  @Override public void hideShot(String idShot, Long timeStamp) {
    shotManager.hideShot(idShot, timeStamp);
  }

  @Override public void unhideShot(String idShot) {
    shotManager.pinShot(idShot);
  }

  @Override public HighlightedShotEntity getHighlightedShot(String idStream) {
    return highlightedManager.getHighlightedShotByIdStream(idStream);
  }

  @Override public void putHighlightShot(HighlightedShotEntity highlightedShotApiEntity) {
    highlightedManager.saveHighLightedShot(highlightedShotApiEntity);
  }

  @Override public void dismissHighlight(String idHighlightedShot) {
    highlightedManager.deleteHighlightedShot(idHighlightedShot);
  }

  @Override public void hideHighlightedShot(String idHighlightedShot) {
    highlightedManager.hideHighlightedShot(idHighlightedShot);
  }

  @Override public void callCtaCheckIn(String idStream) {
    throw new IllegalArgumentException("Should not have local implementation");
  }

  @Override public boolean hasNewFilteredShots(String idStream, String lastTimeFiltered, String idUser) {
    return shotManager.hasNewFilteredShots(idStream, lastTimeFiltered, idUser);
  }

  @Override
  public ProfileShotTimelineEntity getProfileShotTimeline(String idUser, Long maxTimestamp,
      int count) {
    ProfileShotTimelineEntity profileShotTimelineEntity = new ProfileShotTimelineEntity();
    profileShotTimelineEntity.setSinceTimestamp(0);
    profileShotTimelineEntity.setSinceTimestamp(0);
    profileShotTimelineEntity.setShotEntities(shotManager.getProfileShots(idUser, count));
    return profileShotTimelineEntity;
  }

  @Override public List<ShotEntity> getEntitiesNotSynchronized() {
    return shotManager.getHiddenShotNotSynchronized();
  }
}
