package com.shootr.mobile.data.repository.datasource.searchItem;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.SearchApiService;
import com.shootr.mobile.data.entity.SearchItemEntity;
import com.shootr.mobile.data.entity.SearchableEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.SearchableType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceSearchItemDataSource implements ExternalSearchItemDataSource {

  private final SearchApiService searchApiService;

  @Inject public ServiceSearchItemDataSource(SearchApiService searchApiService) {
    this.searchApiService = searchApiService;
  }

  @Override public List<SearchableEntity> getSearch(String query, String[] searchTypes) {
    try {
      ArrayList<SearchableEntity> searchableEntities = new ArrayList<>();
      ArrayList<SearchItemEntity> searchItemEntities = new ArrayList<>(searchApiService.searchItems(query,
          searchTypes));

      for (SearchItemEntity searchItemEntity : searchItemEntities) {
        switch (searchItemEntity.getType()) {
          case SearchableType.STREAM:
            searchableEntities.add(searchItemEntity.getStreamEntity());
            break;
          case SearchableType.USER:
            searchableEntities.add(searchItemEntity.getUserEntity());
            break;
          default:
            break;
        }
      }

      return searchableEntities;
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }

  }
}
