package gm.mobi.android.task.jobs;

import com.path.android.jobqueue.Params;

import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.RegistrationCompletedEvent;

public class RegisterNewUserJob extends CancellableJob {
    private static final int PRIORITY = 1;


    public RegisterNewUserJob(String username, String email, String avatarUrl) {
        super(new Params(PRIORITY)
                        .delayInMs(1000)
        );
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    public void onRun() throws Throwable {
        if (isCancelled()) return;
        BusProvider.getInstance().post(new RegistrationCompletedEvent());
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
