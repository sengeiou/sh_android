package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends BagdadBaseJob<UserInfoResultEvent> {

    private static final int PRIORITY = 3; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    BagdadService service;

    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;

    private Long userId;
    private User currentUser;

    @Inject public GetUserInfoJob(Application application, Bus bus, SQLiteOpenHelper dbHelper, BagdadService service,
      NetworkUtil networkUtil1, UserManager userManager, FollowManager followManager, TeamManager teamManager) {
        super(new Params(PRIORITY), application, bus, networkUtil1);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
        setOpenHelper(dbHelper);
    }

    public void init(Long userId, User currentUser) {
        this.userId = userId;
        this.currentUser = currentUser;
    }

    @Override public void run() throws SQLException, IOException {
        User userFromLocalDatabase = getUserFromDatabase();
        if (userFromLocalDatabase != null) {
            postSuccessfulEvent(new UserInfoResultEvent(userFromLocalDatabase, 0, "")); //TODO coger datos buenos: relationship y team
        } else {
            Timber.d("User with id %d not found in local database. Retrieving from the service...", userId);
        }

        User userFromService = getUserFromService();
        postSuccessfulEvent(new UserInfoResultEvent(userFromService, 0, "")); //TODO coger datos buenos: relationship y team

        if (userFromLocalDatabase != null) {
            Timber.d("Obtained user from server found in database. Updating database.");
            userManager.saveUser(userFromService);
        }
    }

    private User getUserFromDatabase() {
        return userManager.getUserByIdUser(userId);
    }

    private User getUserFromService() throws IOException {
        return service.getUserByIdUser(userId);
    }

    @Override
    protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        teamManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
