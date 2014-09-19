package gm.mobi.android.task.jobs;

import android.content.Context;

import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.task.events.loginregister.RegistrationCompletedEvent;

public class RegisterNewUserJob extends CancellableJob {
    private static final int PRIORITY = 1;

    @Inject Bus bus;

    public RegisterNewUserJob(Context context, String username, String email, String avatarUrl) {
        super(new Params(PRIORITY)
                        .delayInMs(1000)
        );
        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    public void onRun() throws Throwable {
        if (isCancelled()) return;
        bus.post(new RegistrationCompletedEvent());
    }

    @Override
    protected void onCancel() {
        //TODO send event with informative exception
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return 3;
    }
}
