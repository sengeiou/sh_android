package gm.mobi.android.ui;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.ui.debug.DebugAppContainer;

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
