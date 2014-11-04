package com.shootr.android.ui;

import dagger.Module;
import dagger.Provides;
import com.shootr.android.ui.debug.DebugAppContainer;
import javax.inject.Singleton;

@Module(
    injects = DebugAppContainer.class,
    complete = false,
    library = true,
    overrides = true
)
public class DebugUiModule {
  @Provides
  @Singleton AppContainer provideAppContainer(DebugAppContainer debugAppContainer) {
    return debugAppContainer;
  }

}
