package com.shootr.mobile.data.repository.remote.cache;

import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LastStreamVisitCache {

  private final DualCache<Long> lastVisitCache;

  @Inject public LastStreamVisitCache(DualCache<Long> lastVisitCache) {
    this.lastVisitCache = lastVisitCache;
  }

  public Long getLastVisit(String idStream) {
    return lastVisitCache.get(idStream);
  }

  public void putLastVisit(String idStream, Long timestamp) {
    lastVisitCache.put(idStream, timestamp);
  }
}
