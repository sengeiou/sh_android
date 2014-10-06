package gm.mobi.android.task.jobs.loginregister;


import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.sql.SQLException;

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

    Application app;
    NetworkUtil networkUtil;
    Bus bus;
    BagdadService service;
    UserManager userManager;
    SQLiteDatabase db;

    @Inject public LoginUserJob(Application context, NetworkUtil networkUtil, Bus bus, BagdadService service, UserManager userManager) {
        super(new Params(PRIORITY));
        this.app = context;
        this.networkUtil = networkUtil;
        this.bus = bus;
        this.service = service;
        this.userManager = userManager;
    }

    @Override
    public void onAdded() {
        /* no-op */
    }

    public void init( String usernameEmail, String password){
        this.usernameEmail = usernameEmail;
        this.password = password;
    }



    @Override
    protected void createDatabase() {
       db = createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers() {
        userManager.setDataBase(db);
    }

    @Override
    protected void run() throws SQLException, IOException {
       if (isCancelled()) return;
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }
        try {
            User user = service.login(usernameEmail, password);
            if (user != null) {
                // Store user in database
                userManager.saveUser(user);
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
