package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class TimelineJob extends BagdadBaseJob<BagdadBaseJob.SuccessEvent> {

    public static final int RETRIEVE_INITIAL = 0;
    public static final int RETRIEVE_NEWER = 1;
    public static final int RETRIEVE_OLDER = 2;

    private static final int PRIORITY = 4;

    private BagdadService service;
    private ShotManager shotManager;
    private FollowManager followManager;

    private int shotRetrieveType;
    private User currentUser;

    @Inject
    public TimelineJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        setOpenHelper(dbHelper);
        this.service = service;
        this.shotManager = shotManager;
        this.followManager = followManager;
    }

    public void init(User currentUser, int retrieveType) {
        this.shotRetrieveType = retrieveType;
        this.currentUser = currentUser;
    }

    @Override
    protected void run() throws SQLException, IOException {
        switch (shotRetrieveType) {
            case RETRIEVE_INITIAL:
                retrieveInitial();
                break;
            case RETRIEVE_NEWER:
                retrieveNewer();
                break;
            case RETRIEVE_OLDER:
                retrieveOlder();
                break;
        }
    }

    private void retrieveInitial() throws IOException, SQLException {
        // Try to get timeline from database

        List<Shot> localShots = shotManager.retrieveTimelineWithUsers();
        if (localShots != null && localShots.size() > 0) {
            // Got them already :)
            postSuccessfulEvent(new ShotsResultEvent(localShots));
        } else {
            // If we don't have any, check the server
            List<Shot> remoteShots = service.getShotsByUserIdList(getFollowingIds(), 0L);
            shotManager.saveShots(remoteShots);
            // Retrieve from db because we need the user objects associated to the shots
            List<Shot> shotsWithUsersFromServer = shotManager.retrieveTimelineWithUsers();
            postSuccessfulEvent(new ShotsResultEvent(shotsWithUsersFromServer));
        }
    }

    private void retrieveNewer() throws IOException, SQLException {
        Long lastModifiedDate = shotManager.getLastModifiedDate(GMContract.ShotTable.TABLE);
        List<Shot> newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);
        //TODO what if newshots is empty?
        shotManager.saveShots(newShots);
        List<Shot> updatedTimeline = shotManager.retrieveTimelineWithUsers();
        postSuccessfulEvent(new NewShotsReceivedEvent(updatedTimeline));
    }

    private void retrieveOlder() throws IOException, SQLException {
        Long firstModifiedDate = shotManager.getFirstModifiedDate(GMContract.ShotTable.TABLE);
        List<Shot> olderShots = service.getOlderShots(getFollowingIds(), firstModifiedDate);
        shotManager.saveShots(olderShots);

        List<Shot> olderShotsWithUsers = shotManager.retrieveOldOrNewTimeLineWithUsers(olderShots);
        postSuccessfulEvent(new OldShotsReceivedEvent(olderShotsWithUsers));
    }

    private List<Long> getFollowingIds() throws SQLException {
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
        return true;
    }

}
