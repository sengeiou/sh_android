package com.shootr.mobile.domain.repository.discover;

import com.shootr.mobile.domain.model.discover.Discovered;
import java.util.List;

public interface InternalDiscoveredRepository {

  List<Discovered> getDiscovered();

  void putDiscovereds(List<Discovered> discovereds);
}
