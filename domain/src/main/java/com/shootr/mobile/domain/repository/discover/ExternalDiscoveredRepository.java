package com.shootr.mobile.domain.repository.discover;

import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.domain.model.discover.Discovered;
import java.util.List;

public interface ExternalDiscoveredRepository {

  List<Discovered> getDiscovered(String locale, String[] streamTypes, String[] discoverTypes);

  DiscoverTimeline getDiscoverTimeline(String locale, String[] streamTypes);
}
