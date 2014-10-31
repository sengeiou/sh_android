package gm.mobi.android.task.jobs.shots;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.shots.PostNewShotResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class PostNewShotJob extends BagdadBaseJob<PostNewShotResultEvent> {

    private static final int PRIORITY = 5;

    BagdadService service;

    private UserEntity currentUser;
    private String comment;

    @Inject public PostNewShotJob(Application application, NetworkUtil networkUtil, Bus bus, BagdadService service) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
    }

    public void init(UserEntity currentUser, String comment){
        this.currentUser = currentUser;
        this.comment = comment;
    }

    @Override
    protected void run() throws SQLException, IOException {
        ShotEntity postedShot = service.postNewShot(currentUser.getIdUser(), comment);
        postSuccessfulEvent(new PostNewShotResultEvent(postedShot));
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
