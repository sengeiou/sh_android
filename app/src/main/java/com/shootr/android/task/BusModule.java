package com.shootr.android.task;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.task.jobs.follows.GetFollowingsJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.loginregister.LoginUserJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.timeline.TimelineJob;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.registro.EmailLoginActivity;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.activities.registro.FacebookRegistroActivity;
import com.shootr.android.ui.fragments.ProfileFragment;
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

                LoginUserJob.class,
          
                ProfileFragment.class,
                ProfileContainerActivity.class,

                TimelineJob.class,
        },
        complete = false
)
public class BusModule {

    @Provides @Singleton Bus provideBus() {
        return new MainThreadBus();
    }

    public static class MainThreadBus extends Bus {

        private final Handler mainThread = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        post(event);
                    }
                });
            }
        }
    }
}
