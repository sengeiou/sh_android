package gm.mobi.android.task;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.task.jobs.follows.GetUsersFollowingJob;
import gm.mobi.android.task.jobs.loginregister.GetFacebookProfileJob;
import gm.mobi.android.task.jobs.loginregister.LoginUserJob;
import gm.mobi.android.task.jobs.loginregister.RegisterNewUserJob;
import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.activities.registro.EmailLoginActivity;
import gm.mobi.android.ui.activities.registro.EmailRegistrationActivity;
import gm.mobi.android.ui.activities.registro.FacebookRegistroActivity;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.ProfileFragment;

@Module(
        injects = {
                EmailLoginActivity.class,
                EmailRegistrationActivity.class,
                FacebookRegistroActivity.class,

                GetUsersFollowingJob.class,
                GetFacebookProfileJob.class,
                GetUserInfoJob.class,
                GetFollowingsJob.class,

                LoginUserJob.class,

                ProfileFragment.class,
                ProfileContainerActivity.class,

                RegisterNewUserJob.class,

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
