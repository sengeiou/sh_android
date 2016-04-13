package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.nicer.NicerDataSource;
import com.shootr.mobile.data.repository.datasource.nicer.ServiceNicerDatasource;
import com.shootr.mobile.data.repository.remote.SyncNicerRepository;
import com.shootr.mobile.domain.repository.NicerRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncNicerRepository.class, ServiceNicerDatasource.class
  },
  complete = false,
  library = true) public class NicerRepositoryModule {

    @Provides @Singleton NicerRepository provideNicerRepository(SyncNicerRepository nicerRepository) {
        return nicerRepository;
    }

    @Provides @Singleton NicerDataSource provideNicerDataSource(ServiceNicerDatasource serviceNicerDatasource) {
        return serviceNicerDatasource;
    }
}
