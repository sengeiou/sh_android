package com.shootr.mobile.data.repository.remote.cache;

import com.shootr.mobile.data.repository.datasource.CachedDataSource;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LandingStreamsCache implements CachedDataSource {

  private static final String LANDING_STREAMS = "landing_stream";
  private final DualCache<LandingStreams> landingStreamsLruCache;

  @Inject public LandingStreamsCache(DualCache<LandingStreams> landingStreamsLruCache) {
    this.landingStreamsLruCache = landingStreamsLruCache;
  }

  public LandingStreams getLandingStreams() {
    return landingStreamsLruCache.get(LANDING_STREAMS);
  }

  public void putLandingStreams(LandingStreams landingStreams) {
    landingStreamsLruCache.invalidate();
    landingStreamsLruCache.put(LANDING_STREAMS, landingStreams);
  }

  @Override public boolean isValid() {
    return false;
  }

  @Override public void invalidate() {
    landingStreamsLruCache.invalidate();
  }
}
