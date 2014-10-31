package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.ShootrService;
import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.task.jobs.ShootrBaseJob.SuccessEvent;

import java.sql.SQLException;
import java.util.List;

public abstract class TimelineJob<T> extends ShootrBaseJob<SuccessEvent> {

    private static final int PRIORITY = 4;

    private ShootrService service;
    private ShotManager shotManager;
    private FollowManager followManager;

    private UserEntity currentUser;

    public TimelineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil, ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        setOpenHelper(dbHelper);
        this.service = service;
        this.shotManager = shotManager;
        this.followManager = followManager;
    }

    public void init(UserEntity currentUser) {
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
