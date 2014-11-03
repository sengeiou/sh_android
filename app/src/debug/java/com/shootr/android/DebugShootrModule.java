package com.shootr.android;


import dagger.Module;
import com.shootr.android.data.DebugDataModule;
import com.shootr.android.ui.DebugUiModule;

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
