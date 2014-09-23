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
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.timeline.FollowsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

public class FollowingsJob extends CancellableJob{

    @Inject Application app;
    @Inject NetworkUtil networkUtil;
    @Inject Bus bus;
    @Inject SQLiteOpenHelper mDbHelper;
    @Inject BagdadService service;

    private static final int PRIORITY = 6; //TODO Define next values for our queue
    private static final int RETRY_ATTEMPTS = 3;
    private Integer idUser;
    private Context context;
    public SQLiteDatabase db;

    public FollowingsJob(Context context, Integer idUser, SQLiteDatabase db){
        super(new Params(PRIORITY));
        this.context = context;
        this.idUser = idUser;
        GolesApplication.get(context).inject(this);

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (isCancelled()) return;
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            List<Follow> follows = service.getFollows(idUser,context, mDbHelper.getWritableDatabase(), UserDtoFactory.GET_FOLLOWING);
            if(follows != null){
                FollowManager.saveFollows(mDbHelper.getWritableDatabase(), follows);
                FollowsResultEvent fResultEvent = new FollowsResultEvent(FollowsResultEvent.STATUS_SUCCESS);
                fResultEvent.setFollows(follows);
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

    private void sendCredentialError() {
        FollowsResultEvent fResultEvent = new FollowsResultEvent(FollowsResultEvent.STATUS_INVALID);
        bus.post(fResultEvent.setInvalid());
    }

    private void sendServerError(Exception e) {
        FollowsResultEvent fResultEvent = new FollowsResultEvent(FollowsResultEvent.STATUS_SERVER_FAILURE);
        bus.post(fResultEvent.setServerError(e));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }
}
