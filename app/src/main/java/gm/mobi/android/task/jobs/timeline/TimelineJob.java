package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class TimelineJob extends CancellableJob {

    public static final int RETRIEVE_INITIAL = 0;
    public static final int RETRIEVE_NEWER = 1;
    public static final int RETRIEVE_OLDER = 2;

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;

    private Application app;
    private NetworkUtil networkUtil;
    private Bus bus;
    private BagdadService service;
    private ShotManager shotManager;
    private FollowManager followManager;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private int shotRetrieveType;
    private User currentUser;

    @Inject
    public TimelineJob(Application context,Bus bus, BagdadService service, NetworkUtil networkUtil, ShotManager shotManager,FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(new Params(PRIORITY));
        this.app = context;
        this.bus = bus;
        this.dbHelper = dbHelper;
        this.service = service;
        this.shotManager = shotManager;
        this.followManager = followManager;
        this.networkUtil = networkUtil;
    }

    public void init(User currentUser, int retrieveType) {
        this.shotRetrieveType = retrieveType;
        this.currentUser = currentUser;
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    protected void createDatabase() {
        db = createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers() {
        followManager.setDataBase(db);
        shotManager.setDataBase(db);
    }

    @Override
    protected void run() throws SQLException, IOException {
        if (isCancelled()) return;
        try {
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
        } catch (SQLiteException | IOException e) {
            sendServerError(shotRetrieveType, e);
        }
    }

    private boolean checkNetwork() {
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        }
        return true;
    }

    private void retrieveInitial() throws IOException, SQLException {
        // Try to get timeline from database

        List<Shot> localShots = shotManager.retrieveTimelineWithUsers();
        if (localShots != null && localShots.size() > 0) {
            // Got them already :)
            bus.post(new ShotsResultEvent(ShotsResultEvent.STATUS_SUCCESS).setSuccessful(localShots));
            return;
        }

        // If we don't have any, check the server
        if (!checkNetwork()) return;
        List<Shot> remoteShots = service.getShotsByUserIdList(getFollowingIds(), 0L);
        if (remoteShots != null) {
            shotManager.saveShots(remoteShots);
            // Retrieve from db because we need the user objects associated to the shots
            List<Shot> shotsWithUsers = shotManager.retrieveTimelineWithUsers();
            bus.post(new ShotsResultEvent(ShotsResultEvent.STATUS_SUCCESS).setSuccessful(shotsWithUsers));
        } else {
            sendServerError(RETRIEVE_INITIAL, null);
        }
    }


    private void retrieveNewer() throws IOException, SQLException {
        if (!checkNetwork()) return;
        Long lastModifiedDate = shotManager.getLastModifiedDate(GMContract.ShotTable.TABLE);
        List<Shot> newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);

        if (newShots != null) {
            shotManager.saveShots(newShots);
            List<Shot> updatedTimeline = shotManager.retrieveTimelineWithUsers();
            NewShotsReceivedEvent resultEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_SUCCESS);
            resultEvent.setSuccessful(updatedTimeline);
            resultEvent.setNewShotsCount(newShots.size());
            bus.post(resultEvent);
        } else {
            sendServerError(RETRIEVE_NEWER, null);
        }
    }

    private void retrieveOlder() throws IOException, SQLException {
        if (!checkNetwork()) return;
        Long firstModifiedDate = shotManager.getFirstModifiedDate(GMContract.ShotTable.TABLE);
        List<Shot> oldShots = service.getOlderShots(getFollowingIds(), firstModifiedDate);
        if (oldShots != null) {
            shotManager.saveShots(oldShots);
            List<Shot> shotsWithUsers = shotManager.retrieveOldOrNewTimeLineWithUsers(oldShots);
            bus.post(new OldShotsReceivedEvent(OldShotsReceivedEvent.STATUS_SUCCESS).setSuccessful(shotsWithUsers));
        } else {
            sendServerError(RETRIEVE_OLDER, null);
        }
    }


    private List<Long> getFollowingIds() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(currentUser.getIdUser());
    }

    @Override
    protected void onCancel() {
        /* no-op */
    }

    private boolean checkConnection() {
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }


    private void sendCredentialError(int shotRetrieveType) {
        switch (shotRetrieveType) {
            case RETRIEVE_INITIAL:
                ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_INVALID);
                bus.post(fResultEvent.setInvalid());
                break;
            case RETRIEVE_NEWER:
                NewShotsReceivedEvent newShotsReceivedEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_INVALID);
                bus.post(newShotsReceivedEvent.setInvalid());
                break;
            case RETRIEVE_OLDER:
                OldShotsReceivedEvent oldShotsReceivedEvent = new OldShotsReceivedEvent(OldShotsReceivedEvent.STATUS_INVALID);
                bus.post(oldShotsReceivedEvent.setInvalid());
                break;
        }
    }

    private void sendServerError(int shotRetrieveType, Exception e) {
        switch (shotRetrieveType) {
            case RETRIEVE_INITIAL:
                ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_SERVER_FAILURE);
                bus.post(fResultEvent.setServerError(e));
                break;
            case RETRIEVE_NEWER:
                NewShotsReceivedEvent newShotsReceivedEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_SERVER_FAILURE);
                bus.post(newShotsReceivedEvent.setInvalid());
                break;
            case RETRIEVE_OLDER:
                OldShotsReceivedEvent oldShotsReceivedEvent = new OldShotsReceivedEvent(OldShotsReceivedEvent.STATUS_SERVER_FAILURE);
                bus.post(oldShotsReceivedEvent.setInvalid());
                break;
        }

    }


    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }
}
