package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class TimelineJob<T> extends BagdadBaseJob<BagdadBaseJob.SuccessEvent> {

    private static final int PRIORITY = 4;

    private BagdadService service;
    private ShotManager shotManager;
    private FollowManager followManager;

    private User currentUser;

    public TimelineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        setOpenHelper(dbHelper);
        this.service = service;
        this.shotManager = shotManager;
        this.followManager = followManager;
    }

    public void init(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Long> getFollowingIds() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(currentUser.getIdUser());
    }


    @Override
    protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        shotManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
