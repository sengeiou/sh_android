package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.repository.remote.cache.DiscoverTimelineCache;
import com.shootr.mobile.data.repository.remote.cache.DiscoveredCache;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalDiscoveredRepository implements InternalDiscoveredRepository {

  private final DiscoveredCache discoveredCache;
  private final DiscoverTimelineCache discoverTimelineCache;

  @Inject public LocalDiscoveredRepository(DiscoveredCache discoveredCache,
      DiscoverTimelineCache discoverTimelineCache) {
    this.discoveredCache = discoveredCache;
    this.discoverTimelineCache = discoverTimelineCache;
  }

  @Override public List<Discovered> getDiscovered() {
    return discoveredCache.getDiscovered();
  }

  @Override public DiscoverTimeline getDiscoverTimeline() {
    return discoverTimelineCache.getDiscovered();
  }

  @Override public void putDiscovereds(List<Discovered> discovereds) {
    discoveredCache.putDiscovered(discovereds);
  }

  @Override public void putDiscoverTimeline(DiscoverTimeline discover) {
    discoverTimelineCache.putDiscovered(discover);
  }
}
