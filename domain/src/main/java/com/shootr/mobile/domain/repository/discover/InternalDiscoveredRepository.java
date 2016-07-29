package com.shootr.mobile.domain.repository.discover;

import com.shootr.mobile.domain.Discovered;
import java.util.List;

public interface InternalDiscoveredRepository {

  List<Discovered> getDiscovered();

  void putDiscovereds(List<Discovered> discovereds);
}
