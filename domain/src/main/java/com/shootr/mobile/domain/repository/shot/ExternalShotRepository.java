package com.shootr.mobile.domain.repository.shot;

import com.shootr.mobile.domain.exception.UserAlreadyCheckInRequestException;
import com.shootr.mobile.domain.exception.UserCannotCheckInRequestException;

public interface ExternalShotRepository extends ShotRepository {

  void shareShot(String idShot);

  void unhideShot(String idShot);

  void highlightShot(String idShot);

  void callCtaCheckIn(String idStream) throws UserAlreadyCheckInRequestException,
      UserCannotCheckInRequestException;
}
