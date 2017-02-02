package com.shootr.mobile.data.repository.datasource.shot;

import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.List;

public interface ShotDataSource extends SyncableDataSource<ShotEntity> {

  ShotEntity putShot(ShotEntity shotEntity);

  void putShots(List<ShotEntity> shotEntities);

  List<ShotEntity> getShotsForStreamTimeline(StreamTimelineParameters parameters);

  ShotEntity getShot(String shotId, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getReplies(String shotId, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getStreamMediaShots(String idStream, List<String> userId, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes);

  ShotDetailEntity getShotDetail(String idShot, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes);

  List<ShotEntity> getAllShotsFromUserAndDate(String userId, Long currentOldestDate,
      String[] streamTypes, String[] shotTypes);

  void shareShot(String idShot);

  void deleteShot(String idShot);

  List<ShotEntity> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters);

  void deleteShotsByIdStream(String idStream);

  void hideShot(String idShot, Long timestamp);

  void unhideShot(String idShot);

  HighlightedShotEntity getHighlightedShot(String idStream);

  HighlightedShotEntity highlightShot(String idShot);

  void putHighlightShot(HighlightedShotEntity highlightedShotEntity);

  void dismissHighlight(String idHighlightedShot);

  void hideHighlightedShot(String idHighlightedShot);

  void callCtaCheckIn(String idStream) throws UserCannotCheckInRequestException,
      UserAlreadyCheckInRequestException;

  boolean hasNewFilteredShots(String idStream, String lastTimeFiltered);
}
