package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.searchItem.ExternalSearchItemDataSource;
import com.shootr.mobile.data.repository.datasource.searchItem.ServiceSearchItemDataSource;
import com.shootr.mobile.data.repository.remote.SyncSearchItemRepository;
import com.shootr.mobile.domain.repository.searchItem.ExternalSearchItemRepository;
import dagger.Module;
import dagger.Provides;


@Module(
    injects = {
        SyncSearchItemRepository.class, ServiceSearchItemDataSource.class
    },
    complete = false,
    library = true) public class SearchRepositoryModule {

  @Provides ExternalSearchItemDataSource provideExternalSearchDatasource(ServiceSearchItemDataSource serviceSearchItemDataSource) {
    return serviceSearchItemDataSource;
  }

  @Provides
  ExternalSearchItemRepository provideRemoteSearchRepository(SyncSearchItemRepository syncSearchItemRepository) {
    return syncSearchItemRepository;
  }
}
