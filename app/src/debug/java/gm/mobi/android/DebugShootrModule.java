package gm.mobi.android;


import dagger.Module;
import gm.mobi.android.data.DebugDataModule;
import gm.mobi.android.ui.DebugUiModule;

@Module(
        addsTo = ShootrModule.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class
        },
        overrides = true
)
public final class DebugShootrModule {
}
