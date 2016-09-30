package com.shootr.mobile;

import com.shootr.mobile.data.DebugDataModule;
import com.shootr.mobile.stetho.StethoModule;
import com.shootr.mobile.ui.DebugUiModule;
import dagger.Module;

@Module(
        addsTo = ShootrModule.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class,
                StethoModule.class,
        },
        overrides = true
)
public final class DebugShootrModule {
}
