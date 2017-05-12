package com.shootr.mobile.domain.repository.shot;

import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.List;

public interface ExternalShotRepository extends ShotRepository {

  void unhideShot(String idShot);

  void highlightShot(String idShot);

  void callCtaCheckIn(String idStream) throws UserAlreadyCheckInRequestException,
      UserCannotCheckInRequestException;

  List<Shot> updateImportantShots(StreamTimelineParameters streamTimelineParameters);
}
