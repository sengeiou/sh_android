package com.shootr.android.data.repository.dagger;

import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.util.DatabaseVersionUtils;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
