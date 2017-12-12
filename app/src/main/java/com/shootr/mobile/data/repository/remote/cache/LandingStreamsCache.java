package com.shootr.mobile.data.repository.remote.cache;

import android.support.v4.util.LruCache;
import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LandingStreamsCache implements CachedDataSource {

  private static final String LANDING_STREAMS = "LANDING_STREAMS";
  private final LruCache<String, LandingStreams> landingStreamsLruCache;

  @Inject public LandingStreamsCache(LruCache<String, LandingStreams> landingStreamsLruCache) {
    this.landingStreamsLruCache = landingStreamsLruCache;
  }

  public LandingStreams getLandingStreams() {
    return landingStreamsLruCache.get(LANDING_STREAMS);
  }
  
  public void putLandingStreams(LandingStreams landingStreams) {
    landingStreamsLruCache.evictAll();
    landingStreamsLruCache.put(LANDING_STREAMS, landingStreams);
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    landingStreamsLruCache.evictAll();
  }
}
