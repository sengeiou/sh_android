package com.shootr.mobile.data.repository.datasource.discovered;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.DiscoveredApiService;
import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceDiscoveredDatasource implements ExternalDiscoveredDatasource {

  private final DiscoveredApiService discoveredApiService;

  @Inject public ServiceDiscoveredDatasource(DiscoveredApiService discoveredApiService) {
    this.discoveredApiService = discoveredApiService;
  }

  @Override public List<DiscoveredEntity> getDiscovered(String locale, String[] streamTypes,
      String[] discoveredTypes) {
    try {
      return discoveredApiService.getDiscoveredList(locale, streamTypes, discoveredTypes);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
