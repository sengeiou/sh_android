package gm.mobi.android;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.integrationtests.GetPeopleJobTest;
import gm.mobi.android.integrationtests.ShotDtoFactoryTest;
import gm.mobi.android.integrationtests.TestOpenHelper;
import gm.mobi.android.integrationtests.UserDtoFactoryTest;
import javax.inject.Singleton;

@Module(
  includes = GolesModule.class,
  overrides = true,
  injects = { ShotDtoFactoryTest.class, TestGolesApplication.class, UserDtoFactoryTest.class, GetPeopleJobTest.class}
)
public class GolesTestModule {

    @Provides @Singleton SQLiteOpenHelper provideSQLiteOpenHelper(Application application) {
        return new TestOpenHelper(application);
    }

}
