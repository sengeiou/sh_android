package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.data.mapper.DiscoverTimelineMapper;
import com.shootr.mobile.data.mapper.DiscoveredMappper;
import com.shootr.mobile.data.repository.datasource.discovered.ExternalDiscoveredDatasource;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.repository.discover.ExternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncDiscoveredRepository implements ExternalDiscoveredRepository {

  private final ExternalDiscoveredDatasource externalDiscoveredDatasource;
  private final InternalDiscoveredRepository internalDiscoveredRepository;
  private final DiscoveredMappper discoveredMappper;
  private final DiscoverTimelineMapper discoverTimelineMapper;

  @Inject public SyncDiscoveredRepository(ExternalDiscoveredDatasource externalDiscoveredDatasource,
      InternalDiscoveredRepository internalDiscoveredRepository,
      DiscoveredMappper discoveredMappper, DiscoverTimelineMapper discoverTimelineMapper) {
    this.externalDiscoveredDatasource = externalDiscoveredDatasource;
    this.internalDiscoveredRepository = internalDiscoveredRepository;
    this.discoveredMappper = discoveredMappper;
    this.discoverTimelineMapper = discoverTimelineMapper;
  }

  @Override public List<Discovered> getDiscovered(String locale, String[] streamTypes,
      String[] discoverTypes) {
    List<DiscoveredEntity> discoveredEntities =
        externalDiscoveredDatasource.getDiscovered(locale, streamTypes, discoverTypes);
    List<Discovered> discovereds = discoveredMappper.map(discoveredEntities);
    internalDiscoveredRepository.putDiscovereds(discovereds);
    return discovereds;
  }

  @Override public DiscoverTimeline getDiscoverTimeline(String locale, String[] streamTypes) {
    return discoverTimelineMapper.map(externalDiscoveredDatasource.getDiscoverTimeline(locale, streamTypes));
  }
}
