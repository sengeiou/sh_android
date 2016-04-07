package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.util.DatabaseVersionUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    DatabaseVersionUtils.class
  },
  complete = false,
  library = true)
public class DatabaseUtilsModule {

    @Provides @Singleton DatabaseUtils provideDatabaseUtils(DatabaseVersionUtils databaseVersionUtils) {
        return databaseVersionUtils;
    }

}
