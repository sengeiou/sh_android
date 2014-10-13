package gm.mobi.android.task.jobs.loginregister;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.loginregister.LoginResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class LoginUserJob extends BagdadBaseJob<LoginResultEvent> {

    private static final int PRIORITY = 8; //TODO definir valores estáticos para determinados casos

    private String usernameEmail;
    private String password;

    BagdadService service;
    UserManager userManager;

    @Inject public LoginUserJob(Application context, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper dbHelper, BagdadService service, UserManager userManager) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.setOpenHelper(dbHelper);
    }

    public void init(String usernameEmail, String password) {
        this.usernameEmail = usernameEmail;
        this.password = password;
    }

    @Override
    protected void run() throws SQLException, IOException {
        try {
            User user = service.login(usernameEmail, password);
            userManager.saveUser(user);
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


    @Override
    protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    public static class CredentialErrorEvent {
    }
}
