package com.shootr.mobile.data.repository.remote.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.shootr.mobile.domain.model.discover.Discovered;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DiscoveredCache {

  public static final String GET_DISCOVERED = "getDiscovered";
  public static final int GET_DISCOVER_KEEP_ALIVE_SECONDS = 60 * 60 * 1000;
  private final QNCache<List<Discovered>> discoveredCache;

  @Inject public DiscoveredCache(QNCache discoveredCache) {
    this.discoveredCache = discoveredCache;
  }

  public List<Discovered> getDiscovered() {
    List<Discovered> discoveredList = discoveredCache.get(GET_DISCOVERED);
    if (discoveredList != null) {
      List<Discovered> cachedPeople = new ArrayList<>(discoveredList.size());
      cachedPeople.addAll(discoveredList);
      return cachedPeople;
    } else {
      return Collections.emptyList();
    }
  }

  public void invalidatePeople() {
    discoveredCache.remove(GET_DISCOVERED);
  }

  public void putDiscovered(List<Discovered> discovereds) {
    discoveredCache.set(GET_DISCOVERED, discovereds, GET_DISCOVER_KEEP_ALIVE_SECONDS);
  }
}
