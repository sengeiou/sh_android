package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.LocalSynchroDataSource;
import com.shootr.mobile.data.repository.datasource.SynchroDataSource;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        LocalSynchroDataSource.class
    },
    complete = false,
    library = true) public class SynchroModule {

  @Provides @Singleton SynchroDataSource provideLocalSynchroDataSource(LocalSynchroDataSource localSynchroDataSource) {
    return localSynchroDataSource;
  }
}

