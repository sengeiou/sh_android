package com.shootr.android.data.background.dagger;

import com.shootr.android.data.background.BackgroundShotSender;
import com.shootr.android.data.background.ShotDispatcherBackgroundService;
import com.shootr.android.domain.interactor.shot.PostNewShotInteractor;
import com.shootr.android.domain.service.dagger.Background;
import com.shootr.android.domain.service.dagger.Queuer;
import com.shootr.android.domain.service.ShotDispatcher;
import com.shootr.android.domain.service.ShotSender;
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
