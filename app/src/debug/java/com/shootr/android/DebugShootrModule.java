package com.shootr.android;

import com.shootr.android.data.DebugDataModule;
import com.shootr.android.ui.DebugUiModule;
import dagger.Module;

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
