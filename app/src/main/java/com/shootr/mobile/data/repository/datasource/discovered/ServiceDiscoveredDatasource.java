package com.shootr.mobile.data.repository.datasource.discovered;

import com.shootr.mobile.data.api.entity.DiscoveredApiEntity;
import com.shootr.mobile.data.api.entity.mapper.DiscoverTimelineApiEntityMapper;
import com.shootr.mobile.data.api.entity.mapper.DiscoveredApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.DiscoveredApiService;
import com.shootr.mobile.data.entity.DiscoverTimelineEntity;
import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceDiscoveredDatasource implements ExternalDiscoveredDatasource {

  private final DiscoveredApiService discoveredApiService;
  private final DiscoveredApiEntityMapper discoveredApiEntityMapper;
  private final DiscoverTimelineApiEntityMapper discoverTimelineApiEntityMapper;

  @Inject public ServiceDiscoveredDatasource(DiscoveredApiService discoveredApiService,
      DiscoveredApiEntityMapper discoveredApiEntityMapper,
      DiscoverTimelineApiEntityMapper discoverTimelineApiEntityMapper) {
    this.discoveredApiService = discoveredApiService;
    this.discoveredApiEntityMapper = discoveredApiEntityMapper;
    this.discoverTimelineApiEntityMapper = discoverTimelineApiEntityMapper;
  }

  @Override public List<DiscoveredEntity> getDiscovered(String locale, String[] streamTypes,
      String[] discoveredTypes) {
    try {
      ArrayList<DiscoveredApiEntity> discoveredApiEntities = new ArrayList<>(
          discoveredApiService.getDiscoveredList(locale, streamTypes, discoveredTypes));
      return discoveredApiEntityMapper.map(discoveredApiEntities);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public DiscoverTimelineEntity getDiscoverTimeline(String locale, String[] streamTypes) {
    try {
      return discoverTimelineApiEntityMapper.map(discoveredApiService.getDiscoverTimeline(locale));
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
