package com.shootr.android.data.bus;

import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.notifications.gcm.GCMIntentService;
import com.shootr.android.task.jobs.follows.GetFollowingsJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.registro.EmailLoginActivity;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.activities.registro.FacebookRegistroActivity;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,

                FacebookRegistroActivity.class,
                FindFriendsActivity.class,

                GetUsersFollowsJob.class,
                GetUserInfoJob.class,
                GetFollowingsJob.class,

                ProfileFragment.class,
                ProfileContainerActivity.class,

                GCMIntentService.class,
        },
        complete = false
)
public class BusModule {

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
