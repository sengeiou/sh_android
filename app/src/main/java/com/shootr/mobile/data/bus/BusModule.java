package com.shootr.mobile.data.bus;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.notifications.gcm.GCMIntentService;
import com.shootr.mobile.ui.activities.FindFriendsActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.registro.EmailLoginActivity;
import com.shootr.mobile.ui.activities.registro.EmailRegistrationActivity;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    EmailLoginActivity.class, EmailRegistrationActivity.class,

    FindFriendsActivity.class,

    ProfileActivity.class,

    GCMIntentService.class,
  },
  complete = false) public class BusModule {

    @Provides @Singleton BusPublisher provideBusPublisher(@Default Bus backgroundBus, @Main Bus uiBus) {
        return new MultiBusPublisher(uiBus, backgroundBus);
    }

    @Provides @Singleton @Main Bus provideUiBus() {
        return new AndroidBus();
    }

    @Provides @Singleton @Default Bus provideBackgroundBus() {
        return new DefaultBus();
    }
}
