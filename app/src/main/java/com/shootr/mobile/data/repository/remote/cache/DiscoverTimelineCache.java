package com.shootr.mobile.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DiscoverTimelineCache {
  public static final String GET_DISCOVER = "getDiscover";
  public static final int GET_DISCOVER_KEEP_ALIVE_SECONDS = 60 * 60 * 1000;
  private final QNCache<DiscoverTimeline> discoverCache;

  @Inject public DiscoverTimelineCache(QNCache discoverCache) {
    this.discoverCache = discoverCache;
  }

  public DiscoverTimeline getDiscovered() {
    DiscoverTimeline discoverTimeline = discoverCache.get(GET_DISCOVER);
    if (discoverTimeline != null) {
      return discoverTimeline;
    } else {
      return new DiscoverTimeline();
    }
  }

  public void invalidatePeople() {
    discoverCache.remove(GET_DISCOVER);
  }

  public void putDiscovered(DiscoverTimeline discovereds) {
    discoverCache.set(GET_DISCOVER, discovereds, GET_DISCOVER_KEEP_ALIVE_SECONDS);
  }
}
