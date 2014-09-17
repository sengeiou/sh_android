package gm.mobi.android;


import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.data.DataModule;
import gm.mobi.android.task.BusModule;
import gm.mobi.android.ui.UiModule;
import gm.mobi.android.ui.activities.MainActivity;

@Module(
        includes = {
                UiModule.class,
                DataModule.class,
                BusModule.class,
        },
        injects = {
                GolesApplication.class
        }
)
public final class GolesModule {
    private final GolesApplication app;

    public GolesModule(GolesApplication app) {
        this.app = app;
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }
}
