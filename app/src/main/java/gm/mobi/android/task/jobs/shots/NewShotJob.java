package gm.mobi.android.task.jobs.shots;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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

public class NewShotJob extends BagdadBaseJob<Shot> {

    private static final int PRIORITY = 5;

    BagdadService service;

    private User currentUser;
    private String comment;

    @Inject public NewShotJob(Application application, NetworkUtil networkUtil, Bus bus, BagdadService service) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
    }

    public void init(User currentUser, String comment){
        this.currentUser = currentUser;
        this.comment = comment;
    }

    @Override
    protected void run() throws SQLException, IOException {
        Shot postedShot = service.postNewShot(currentUser.getIdUser(), comment);
        postSuccessfulEvent(postedShot);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    @Override protected void createDatabase() {
        /* no-op */
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
    }

}
