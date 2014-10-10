package gm.mobi.android.task.jobs.loginregister;

import android.content.Context;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.task.events.loginregister.RegistrationCompletedEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class RegisterNewUserJob extends BagdadBaseJob {
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
    protected void createDatabase() {
        /* no-op */
    }

    @Override
    protected void setDatabaseToManagers() {
        /* no-op */
    }

    @Override
    protected void run() throws SQLException, IOException {
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
