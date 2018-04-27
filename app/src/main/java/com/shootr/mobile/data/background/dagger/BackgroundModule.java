package com.shootr.mobile.data.background.dagger;

import com.shootr.mobile.data.background.BackgroundShotSender;
import com.shootr.mobile.data.background.ShotDispatcherBackgroundService;
import com.shootr.mobile.data.background.sockets.WebSocketService;
import com.shootr.mobile.domain.service.ShotDispatcher;
import com.shootr.mobile.domain.service.MessageSender;
import com.shootr.mobile.domain.service.dagger.Background;
import com.shootr.mobile.domain.service.dagger.Queuer;
import com.shootr.mobile.ui.FloatingVideoService;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    ShotDispatcherBackgroundService.class, BackgroundShotSender.class, WebSocketService.class,
      FloatingVideoService.class
  },
  complete = false,
  library = true) public class BackgroundModule {

    @Provides @Background MessageSender provideBackgroundShotSender(BackgroundShotSender backgroundShotSender) {
        return backgroundShotSender;
    }

    @Provides @Queuer MessageSender provideQueuerShotSender(ShotDispatcher shotDispatcher) {
        return shotDispatcher;
    }
}
