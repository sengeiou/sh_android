package com.shootr.android;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import com.shootr.android.data.DataModule;
import com.shootr.android.data.background.dagger.BackgroundModule;
import com.shootr.android.data.bus.BusModule;
import com.shootr.android.data.dagger.ApplicationContext;
import com.shootr.android.domain.dagger.TemporaryFilesDir;
import com.shootr.android.notifications.dagger.NotificationModule;
import com.shootr.android.ui.UiModule;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;

@Module(
        includes = {
                UiModule.class,
                DataModule.class,
                BusModule.class,
                NotificationModule.class,
                BackgroundModule.class,
        },
        injects = {
                ShootrApplication.class,
        }
)
public final class ShootrModule {
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
