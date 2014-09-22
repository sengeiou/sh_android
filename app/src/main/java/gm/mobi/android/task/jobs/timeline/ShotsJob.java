package gm.mobi.android.task.jobs.timeline;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
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


    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;
    private Context mContext;
    public SQLiteDatabase mDb;
    public List<Integer> mFollowingUserIds;

    public ShotsJob(Context context, List<Integer> followingUserIds, SQLiteDatabase db){
        super(new Params(PRIORITY));
        mContext = context;
        mFollowingUserIds = followingUserIds;
        GolesApplication.get(mContext).inject(this);
    }


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (isCancelled()) return;
        if (!mNetworkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            List<Shot> shots = service.getShots(mFollowingUserIds, mContext, mDbHelper.getWritableDatabase());
            if(shots != null){
                ShotManager.saveShots(mDbHelper.getWritableDatabase(), shots);
                ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_SUCCESS);
                fResultEvent.setShots(shots);
                fResultEvent.setError(null);
                bus.post(fResultEvent);
            }else{
                sendServerError(null);
            }

        } catch (ServerException e) {
            if (e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError();
            } else {
                sendServerError(e);
            }
        } catch (IOException e) {
            sendServerError(e);
        }

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }


    private void sendCredentialError() {
        ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_INVALID);
        bus.post(fResultEvent.invalid());
    }

    private void sendServerError(Exception e) {
        ShotsResultEvent fResultEvent = new ShotsResultEvent(ShotsResultEvent.STATUS_SERVER_FAILURE);
        bus.post(fResultEvent.serverError(e));
    }


    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }
}
