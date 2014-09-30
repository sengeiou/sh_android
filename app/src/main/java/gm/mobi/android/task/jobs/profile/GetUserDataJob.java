package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.profile.UserResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

public class GetUserDataJob extends CancellableJob {

    private static final int PRIORITY = 7;
    private static final int RETRY_ATTEMPTS = 3;

    private Long idUser;

    @Inject
    Application mApp;
    @Inject
    NetworkUtil mNetworkUtil;
    @Inject
    Bus bus;
    @Inject
    SQLiteOpenHelper mDbHelper;
    @Inject
    BagdadService service;

    public GetUserDataJob(Context context, Long idUser){
        super(new Params(PRIORITY));
        this.idUser = idUser;
        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onAdded() {
        /*no-op*/
    }

    @Override
    public void onRun() throws Throwable {
        if(isCancelled())return;
        if(!mNetworkUtil.isConnected(mApp)){
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try{
            User user = service.getUserByIdUser(idUser);
            //Store user in db
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            UserManager.saveUser(db,user);
            db.close();
            sendSuccess(user);
        }catch(ServerException e){
            if(e.getErrorCode().equals(ServerException.G025)){
                sendCredentialError();
            }else{
                sendServerError(e);
            }
        }catch (IOException e){
            sendServerError(e);
        }
    }


    public void sendSuccess(User user){
        UserResultEvent result = new UserResultEvent(UserResultEvent.STATUS_SUCCESS);
        result.setSuccessful(user);
        bus.post(result);
    }

    public void sendCredentialError(){
        UserResultEvent result = new UserResultEvent(UserResultEvent.STATUS_INVALID);
        bus.post(result);
    }

    public void sendServerError(Exception e){
        UserResultEvent result = new UserResultEvent(UserResultEvent.STATUS_SERVER_FAILURE);
        bus.post(result);
    }

    @Override
    protected void onCancel() {
        /*no-op*/
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}

