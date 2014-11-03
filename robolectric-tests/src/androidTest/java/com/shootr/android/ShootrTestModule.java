package gm.mobi.android;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import dagger.Module;
import dagger.Provides;
import gm.mobi.android.integrationtests.GetPeopleJobTest;
import gm.mobi.android.integrationtests.ShotDtoFactoryTest;
import gm.mobi.android.integrationtests.TestShootrDbOpenHelper;
import gm.mobi.android.integrationtests.UserDtoFactoryTest;
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
