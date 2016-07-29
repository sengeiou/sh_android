package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.repository.remote.cache.DiscoveredCache;
import com.shootr.mobile.domain.Discovered;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalDiscoveredRepository implements InternalDiscoveredRepository {

  private final DiscoveredCache discoveredCache;

  @Inject public LocalDiscoveredRepository(DiscoveredCache discoveredCache) {
    this.discoveredCache = discoveredCache;
  }

  @Override public List<Discovered> getDiscovered() {
    return discoveredCache.getDiscovered();
  }

  @Override public void putDiscovereds(List<Discovered> discovereds) {
    discoveredCache.putDiscovered(discovereds);
  }
}
