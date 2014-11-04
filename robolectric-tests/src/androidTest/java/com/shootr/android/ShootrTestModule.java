package com.shootr.android;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.integrationtests.GetPeopleJobTest;
import com.shootr.android.integrationtests.ShotDtoFactoryTest;
import com.shootr.android.integrationtests.TestShootrDbOpenHelper;
import com.shootr.android.integrationtests.UserDtoFactoryTest;
import javax.inject.Singleton;

@Module(
  includes = ShootrModule.class,
  overrides = true,
  injects = { ShotDtoFactoryTest.class, TestShootrApplication.class, UserDtoFactoryTest.class, GetPeopleJobTest.class}
)
public class ShootrTestModule {

    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application) {
        return new TestShootrDbOpenHelper(application);
    }

}
