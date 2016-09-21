package com.shootr.mobile.domain.repository.shot;

public interface ExternalShotRepository extends ShotRepository {

  void shareShot(String idShot);

  void unhideShot(String idShot);

  void highlightShot(String idShot);
}
