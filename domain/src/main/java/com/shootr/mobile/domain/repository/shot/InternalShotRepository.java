package com.shootr.mobile.domain.repository.shot;

import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.shot.Shot;
import java.util.List;

public interface InternalShotRepository extends ShotRepository {

  void putShots(List<Shot> shotsFromUser);

  void deleteShotsByStream(String idStream);

  void hideHighlightedShot(String idHighlightedShot);

  boolean hasNewFilteredShots(String idStream, String lastTimeFiltered);

  ShotDetail getCachedShotDetail(String idShot);
}
