package com.shootr.mobile.data.repository.datasource.discovered;

import com.shootr.mobile.data.entity.DiscoveredEntity;
import java.util.List;

public interface ExternalDiscoveredDatasource {

  List<DiscoveredEntity> getDiscovered(String locale, String[] streamTypes, String[] discoverTypes);
}
