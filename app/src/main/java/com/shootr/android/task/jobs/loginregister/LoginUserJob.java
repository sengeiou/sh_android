package com.shootr.android.task.jobs.loginregister;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.exception.ServerException;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.loginregister.LoginResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;

import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class LoginUserJob extends ShootrBaseJob<LoginResultEvent> {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos

    private String usernameEmail;
    private String password;

    ShootrService service;
    UserManager userManager;

    @Inject public LoginUserJob(Application context, NetworkUtil networkUtil, @Main Bus bus, ShootrService service,
      UserManager userManager) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
    }

    public void init(String usernameEmail, String password) {
        this.usernameEmail = usernameEmail;
        this.password = password;
    }

    @Override
    protected void run() throws SQLException, IOException {
        try {
            UserEntity user = service.login(usernameEmail, password);
            userManager.saveCurrentUser(user);
            postSuccessfulEvent(new LoginResultEvent(user));
        } catch (ServerException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals(ServerException.G025)) {
                sendCredentialError();
            } else {
                throw e;
            }
        }
    }

    private void sendCredentialError() {
        postCustomEvent(new CredentialErrorEvent());
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    public static class CredentialErrorEvent {
    }
}
