package com.shootr.mobile.domain.repository.nice;

import java.util.List;

public interface InternalNiceShotRepository extends NiceShotRepository {

  boolean isMarked(String idShot);

  void markAll(List<String> nicedIdShots);
}
