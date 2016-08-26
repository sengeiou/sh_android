package com.shootr.mobile.data.repository.datasource.discovered;

import com.shootr.mobile.data.api.entity.DiscoveredApiEntity;
import com.shootr.mobile.data.api.entity.mapper.DiscoveredApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.DiscoveredApiService;
import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceDiscoveredDatasource implements ExternalDiscoveredDatasource {

  private final DiscoveredApiService discoveredApiService;
  private final DiscoveredApiEntityMapper discoveredApiEntityMapper;

  @Inject public ServiceDiscoveredDatasource(DiscoveredApiService discoveredApiService,
      DiscoveredApiEntityMapper discoveredApiEntityMapper) {
    this.discoveredApiService = discoveredApiService;
    this.discoveredApiEntityMapper = discoveredApiEntityMapper;
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
}
