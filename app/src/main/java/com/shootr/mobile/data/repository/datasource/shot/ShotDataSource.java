package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ProfileShotTimelineEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource extends SyncableDataSource<ShotEntity> {

  ShotEntity putShot(ShotEntity shotEntity, String idUserMe);

  void putShotViaSocket(ShotEntity shotEntity, String idQueue);

  void putShots(List<ShotEntity> shotEntities, String idUserMe);

  List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters);

  List<ShotEntity> getShotsForStreamTimelineFiltered(StreamTimelineParameters parameters);

  ShotEntity getShot(String shotId, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getStreamMediaShots(String idStream, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes);

  List<ShotEntity> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes);

  void reshoot(String idShot);

  void undoReshoot(String idShot);

  void deleteShot(String idShot);

  List<ShotEntity> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters);

  List<ShotEntity> updateImportantShots(StreamTimelineParameters parameters);

  void deleteShotsByIdStream(String idStream);

  void hideShot(String idShot, Long timestamp);

  void unhideShot(String idShot);

  HighlightedShotEntity getHighlightedShot(String idStream);

  void putHighlightShot(HighlightedShotEntity highlightedShotEntity);

  void dismissHighlight(String idHighlightedShot);

  void hideHighlightedShot(String idHighlightedShot);

  void callCtaCheckIn(String idStream) throws UserCannotCheckInRequestException,
      UserAlreadyCheckInRequestException;

  boolean hasNewFilteredShots(String idStream, String lastTimeFiltered, String idUser);

  ProfileShotTimelineEntity getProfileShotTimeline(String idUser, Long maxTimestamp, int count);
}
