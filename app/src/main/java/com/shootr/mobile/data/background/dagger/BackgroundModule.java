package com.shootr.mobile.data.background.dagger;

import com.shootr.mobile.data.background.BackgroundShotSender;
import com.shootr.mobile.data.background.ShotDispatcherBackgroundService;
import com.shootr.mobile.domain.service.ShotDispatcher;
import com.shootr.mobile.domain.service.ShotSender;
import com.shootr.mobile.domain.service.dagger.Background;
import com.shootr.mobile.domain.service.dagger.Queuer;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    ShotDispatcherBackgroundService.class,
    BackgroundShotSender.class,
  },
  complete = false,
  library = true
)
public class BackgroundModule {

    @Provides @Background ShotSender provideBackgroundShotSender(BackgroundShotSender backgroundShotSender) {
        return backgroundShotSender;
    }

    @Provides @Queuer ShotSender provideQueuerShotSender(ShotDispatcher shotDispatcher) {
        return shotDispatcher;
    }


}
