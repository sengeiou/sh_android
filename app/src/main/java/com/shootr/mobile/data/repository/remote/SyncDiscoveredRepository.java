package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.data.mapper.DiscoveredMappper;
import com.shootr.mobile.data.repository.datasource.discovered.ExternalDiscoveredDatasource;
import com.shootr.mobile.domain.Discovered;
import com.shootr.mobile.domain.repository.discover.ExternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncDiscoveredRepository implements ExternalDiscoveredRepository {

  private final ExternalDiscoveredDatasource externalDiscoveredDatasource;
  private final InternalDiscoveredRepository internalDiscoveredRepository;
  private final DiscoveredMappper discoveredMappper;

  @Inject public SyncDiscoveredRepository(
      ExternalDiscoveredDatasource externalDiscoveredDatasource,
      InternalDiscoveredRepository internalDiscoveredRepository, DiscoveredMappper discoveredMappper) {
    this.externalDiscoveredDatasource = externalDiscoveredDatasource;
    this.internalDiscoveredRepository = internalDiscoveredRepository;
    this.discoveredMappper = discoveredMappper;
  }

  @Override public List<Discovered> getDiscovered(String locale, String[] streamTypes,
      String[] discoverTypes) {
    List<DiscoveredEntity> discoveredEntities =
        externalDiscoveredDatasource.getDiscovered(locale, streamTypes, discoverTypes);
    List<Discovered> discovereds = discoveredMappper.map(discoveredEntities);
    internalDiscoveredRepository.putDiscovereds(discovereds);
    return discovereds;
  }
}
