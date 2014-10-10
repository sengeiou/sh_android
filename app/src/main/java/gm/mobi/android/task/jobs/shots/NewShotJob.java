package gm.mobi.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.shots.PostNewShotResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class NewShotJob extends BagdadBaseJob {

    private static final int PRIORITY = 5;

    Application app;
    NetworkUtil networkUtil;
    Bus bus;
    BagdadService service;
    private User currentUser;
    private String comment;

    @Inject public NewShotJob(Application context, NetworkUtil networkUtil, Bus bus, BagdadService service) {
        super(new Params(PRIORITY));
        this.app = context;
        this.networkUtil = networkUtil;
        this.bus = bus;
        this.service = service;
    }

    public void init(User currentUser, String comment){
        this.currentUser = currentUser;
        this.comment = comment;
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
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            Shot postedShot = service.postNewShot(currentUser.getIdUser(), comment);
            if (postedShot != null) {
                bus.post(new PostNewShotResultEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(postedShot));
            } else {
                bus.post(new PostNewShotResultEvent(ResultEvent.STATUS_INVALID));
            }
        } catch (IOException e) {
            bus.post(new PostNewShotResultEvent(ResultEvent.STATUS_SERVER_FAILURE).setServerError(e));
        }
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
