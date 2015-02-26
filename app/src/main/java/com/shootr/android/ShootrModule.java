package com.shootr.android;

import android.app.Application;
import android.content.res.Resources;
import com.shootr.android.data.background.dagger.BackgroundModule;
import com.shootr.android.notifications.dagger.NotificationModule;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.DataModule;
import com.shootr.android.data.bus.BusModule;
import com.shootr.android.ui.UiModule;
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

    @Provides Resources provideResources(Application application) {
        return application.getResources();
    }
}
