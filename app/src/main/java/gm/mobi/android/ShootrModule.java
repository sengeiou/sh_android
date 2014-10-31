package gm.mobi.android;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import gm.mobi.android.data.DataModule;
import gm.mobi.android.task.BusModule;
import gm.mobi.android.ui.UiModule;
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
