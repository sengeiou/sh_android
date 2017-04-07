package com.shootr.mobile.domain.repository.discover;

import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.domain.model.discover.Discovered;
import java.util.List;

public interface InternalDiscoveredRepository {

  List<Discovered> getDiscovered();

  DiscoverTimeline getDiscoverTimeline();

  void putDiscovereds(List<Discovered> discovereds);

  void putDiscoverTimeline(DiscoverTimeline discover);
}
