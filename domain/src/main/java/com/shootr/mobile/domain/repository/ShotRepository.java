package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.StreamTimelineParameters;
import java.util.List;

public interface ShotRepository {

  List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters);

  List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters);

  Shot getShot(String shotId, String[] streamTypes, String[] shotTypes);

  List<Shot> getReplies(String shot, String[] streamTypes, String[] shotTypes);

  List<Shot> getMediaByIdStream(String idEvent, List<String> userId, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes);

  List<Shot> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes);

  ShotDetail getShotDetail(String idShot, String[] streamTypes, String[] shotTypes);

  List<Shot> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes);

  List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate, String[] streamTypes,
      String[] shotTypes);

  void putShots(List<Shot> shotsFromUser);

  Shot putShot(Shot shot);

  void shareShot(String idShot);

  void deleteShot(String idShot);

  void deleteShotsByStream(String idStream);

  void hideShot(String idShot, Long timestamp, String[] streamTypes, String[] shotTypes);

  void unhideShot(String idShot);
}
