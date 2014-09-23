package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

public class ShotsJob extends CancellableJob {

    @Inject
    Application app;
    @Inject
    NetworkUtil mNetworkUtil;
    @Inject
    Bus bus;
    @Inject
    SQLiteOpenHelper mDbHelper;
    @Inject
    BagdadService service;

    public static final int FIRST_TIME = 0;
    public static final int NEWER = 1;
    public static final int OLDER = 2;

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;
    private Context mContext;
    public SQLiteDatabase mDb;
    public List<Integer> mFollowingUserIds;
    public int shotRetrieveType;
    public Shot mShot;

    public ShotsJob(Context context, SQLiteDatabase db, int shotRetrieveType, Shot shot){
        super(new Params(PRIORITY));
        mContext = context;
        mDb = db;
        this.mShot = shot;
        this.shotRetrieveType = shotRetrieveType;
        GolesApplication.get(mContext).inject(this);
    }


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        List<Shot> shots = new ArrayList<>();
        if (isCancelled()) return;
        if (!mNetworkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            User user = ((GolesApplication)app).getCurrentUser();
            mFollowingUserIds = FollowManager.getUserFollowingIds(mDb, user.getIdUser());
            //Adding owner user, because I need to retrieve all the shots including my owns
            mFollowingUserIds.add(user.getIdUser());
            switch (shotRetrieveType){
                case FIRST_TIME:
                    shots = service.getShotsByUserIdList(mFollowingUserIds, mContext, mDbHelper.getWritableDatabase());
                    break;
                case NEWER:
                    shots = service.getNewShots(mFollowingUserIds, mContext, mDbHelper.getWritableDatabase(), mShot);
                    break;
                case OLDER:
                    shots = service.getOlderShots(mFollowingUserIds, mContext, mDbHelper.getWritableDatabase(), mShot);
                    break;
            }
            if(shots != null){
                ShotManager.saveShots(mDbHelper.getWritableDatabase(), shots);
                switch(shotRetrieveType){
                    case FIRST_TIME:
                        ShotsResultEvent firstTimeResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_SUCCESS);
                        firstTimeResultEvent.setShots(shots);
                        firstTimeResultEvent.setError(null);
                        bus.post(firstTimeResultEvent);
                        break;
                    case NEWER:
                        NewShotsReceivedEvent newerResultEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_SUCCESS);
                        newerResultEvent.setShots(shots);
                        newerResultEvent.setError(null);
                        bus.post(newerResultEvent);
                        break;
                    case OLDER:
                        OldShotsReceivedEvent olderResultEvent = new OldShotsReceivedEvent(OldShotsReceivedEvent.STATUS_SUCCESS);
                        olderResultEvent.setShots(shots);
                        olderResultEvent.setError(null);
                        bus.post(olderResultEvent);
                        break;
                }

            }else{
                sendServerError(shotRetrieveType,null);
            }

        } catch (ServerException e) {
            if (e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError(shotRetrieveType);
            } else {
                sendServerError(shotRetrieveType, e);
            }
        } catch (IOException e) {
            sendServerError(shotRetrieveType, e);
        }

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }


    private void sendCredentialError(int shotRetrieveType) {
        switch (shotRetrieveType){
            case FIRST_TIME:
                ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_INVALID);
                bus.post(fResultEvent.setInvalid());
                break;
            case NEWER:
                NewShotsReceivedEvent newShotsReceivedEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_INVALID);
                bus.post(newShotsReceivedEvent.setInvalid());
                break;
            case OLDER:
                OldShotsReceivedEvent oldShotsReceivedEvent = new OldShotsReceivedEvent(OldShotsReceivedEvent.STATUS_INVALID);
                bus.post(oldShotsReceivedEvent.setInvalid());
                break;
        }
    }

    private void sendServerError(int shotRetrieveType, Exception e) {
        switch (shotRetrieveType) {
            case FIRST_TIME:
                ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_SERVER_FAILURE);
                bus.post(fResultEvent.setServerError(e));
                break;
            case NEWER:
                NewShotsReceivedEvent newShotsReceivedEvent = new NewShotsReceivedEvent(NewShotsReceivedEvent.STATUS_SERVER_FAILURE);
                bus.post(newShotsReceivedEvent.setInvalid());
                break;
            case OLDER:
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
