package com.shootr.mobile.domain.repository.shot;

import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.List;

public interface ShotRepository {

  List<Shot> getShotsForStreamTimeline(StreamTimelineParameters parameters);

  List<Shot> getShotsForStreamTimelineFiltered(StreamTimelineParameters parameters);

  List<Shot> getUserShotsForStreamTimeline(StreamTimelineParameters timelineParameters);

  Shot getShot(String shotId, String[] streamTypes, String[] shotTypes);

  List<Shot> getReplies(String shot, String[] streamTypes, String[] shotTypes);

  List<Shot> getMediaByIdStream(String idEvent, Long maxTimestamp,
      String[] streamTypes, String[] shotTypes);

  List<Shot> getShotsFromUser(String idUser, Integer limit, String[] streamTypes,
      String[] shotTypes);

  ShotDetail getShotDetail(String idShot, String[] streamTypes, String[] shotTypes);

  List<Shot> getAllShotsFromUser(String userId, String[] streamTypes, String[] shotTypes);

  List<Shot> getAllShotsFromUserAndDate(String userId, Long currentOldestDate, String[] streamTypes,
      String[] shotTypes);

  Shot putShot(Shot shot);

  void deleteShot(String idShot);

  void hideShot(String idShot, Long timestamp, String[] streamTypes, String[] shotTypes);

  HighlightedShot getHighlightedShots(String idStream);

  void dismissHighlightedShot(String idHighlightedShot);
}
