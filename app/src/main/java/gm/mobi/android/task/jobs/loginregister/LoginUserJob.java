package gm.mobi.android.task.jobs.loginregister;


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
import gm.mobi.android.task.events.loginregister.LoginResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

public class LoginUserJob extends CancellableJob {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;
    private String usernameEmail;
    private String password;

    @Inject Application app;
    @Inject NetworkUtil networkUtil;
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
        if (isCancelled()) return;
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            User user = service.login(usernameEmail, password);
            if (user != null) {
                // Store user in database
                SQLiteDatabase wdb = mDbHelper.getWritableDatabase();
                UserManager.saveUser(wdb, user);
                wdb.close();
                bus.post(LoginResultEvent.successful(user));
            } else {
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

    private void sendCredentialError() {
        bus.post(LoginResultEvent.invalid());
    }

    private void sendServerError(Exception e) {
        bus.post(LoginResultEvent.serverError(e));
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
        return RETRY_ATTEMPTS;
    }
}
