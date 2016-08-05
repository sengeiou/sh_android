package com.shootr.mobile;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import com.shootr.mobile.data.DataModule;
import com.shootr.mobile.data.background.dagger.BackgroundModule;
import com.shootr.mobile.data.bus.BusModule;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.notifications.dagger.NotificationModule;
import com.shootr.mobile.ui.UiModule;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;

@Module(
    includes = {
        UiModule.class, DataModule.class, BusModule.class, NotificationModule.class,
        BackgroundModule.class,
    },
    injects = {
        ShootrApplication.class,
    }) public final class ShootrModule {

  private final ShootrApplication app;

  public ShootrModule(ShootrApplication app) {
    this.app = app;
  }

  @Provides @Singleton Application provideApplication() {
    return app;
  }

  @Provides @ApplicationContext Context provideApplicationContext(Application application) {
    return application.getApplicationContext();
  }

  @Provides Resources provideResources(Application application) {
    return application.getResources();
  }

  @Provides @TemporaryFilesDir File provideTemporaryFilesDir(Application application) {
    return application.getExternalCacheDir();
  }
}
