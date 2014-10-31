package gm.mobi.android;


import dagger.Module;
import gm.mobi.android.data.DebugDataModule;
import gm.mobi.android.ui.DebugUiModule;

@Module(
        addsTo = GolesModule.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class
        },
        overrides = true
)
public final class DebugGolesModule {
}
