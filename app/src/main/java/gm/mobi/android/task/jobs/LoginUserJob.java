package gm.mobi.android.task.jobs;


import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.LoginResultEvent;

public class LoginUserJob extends CancellableJob {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMTS = 3;
    private String usernameEmail;
    private String password;

    @Inject Bus bus;
    @Inject SQLiteOpenHelper mDbHelper;
    @Inject BagdadService service;

    public LoginUserJob(Context context, String usernameEmail, String password) {
        super(new Params(PRIORITY));
        this.usernameEmail = usernameEmail;
        this.password = password;

        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    @Override
    public void onRun() throws Throwable {
        if(isCancelled()) return;
        // TODO network available? (ConnectionNotAvailableEvent)
//        BusProvider.getInstance().post(new ConnectionNotAvailableEvent());
        try{
                User user = service.login(usernameEmail, password);
                if(user!=null){
                    UserManager.saveUser(mDbHelper.getWritableDatabase(), user);
                    bus.post(LoginResultEvent.successful(user));
                }
            }catch(ServerException e){
                if(e.getErrorCode().equals(ServerException.V999)){
                    bus.post(LoginResultEvent.serverError(e.getErrorCode(), e.getMessage()));
                }else{
                    bus.post(LoginResultEvent.invalid());
                }
            }
    }

    @Override
    protected void onCancel() {
        /* no-op */
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMTS;
    }
}
