package com.shootr.android;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.DataModule;
import com.shootr.android.task.BusModule;
import com.shootr.android.ui.UiModule;
import javax.inject.Singleton;

@Module(
        includes = {
                UiModule.class,
                DataModule.class,
                BusModule.class,
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
}