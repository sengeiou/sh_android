package com.shootr.android.ui;

import android.os.Handler;
import com.shootr.android.data.PollerEnabled;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.ui.debug.DebugAppContainer;
import dagger.Module;
import dagger.Provides;
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

    @Provides Poller providePoller(@PollerEnabled BooleanPreference pollerEnabled) {
        return new DebugPoller(new Handler(), pollerEnabled);
    }
}
