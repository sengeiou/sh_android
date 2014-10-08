package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SearchPeopleJob extends CancellableJob {

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;
    Application app;
    Bus bus;
    NetworkUtil networkUtil;
    BagdadService service;
    private SQLiteDatabase db;
    private UserManager userManager;
    private FollowManager followManager;
    private String searchString;

    @Inject
    public SearchPeopleJob(Application app, Bus bus, BagdadService service, NetworkUtil networkUtil,
      UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY));
        this.app = app;
        this.bus = bus;
        this.networkUtil = networkUtil;
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
    }

    public void init(String searchString) {
        this.searchString = searchString;
    }

    @Override protected void createDatabase() {
        db = createWritableDb();
    }

    @Override protected void setDatabaseToManagers() {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        if (isCancelled()) return;
        //At first we search in database
        retrieveDataFromDataBase();

        //After looking in Database we look in server
        if(!networkUtil.isConnected(app)){
            bus.post(new ConnectionNotAvailableEvent());
            return;
        }

        try {
            List<User> users = service.searchUsersByNameOrNickName(searchString);
            if (users != null && users.size()>0) {
                bus.post(new SearchPeopleEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(users));
            } else {
                bus.post(new SearchPeopleEvent(ResultEvent.STATUS_INVALID));
            }
        } catch (IOException e) {
            bus.post(new SearchPeopleEvent(ResultEvent.STATUS_SERVER_FAILURE).setServerError(e));
        }

    }

    public void retrieveDataFromDataBase(){

        List<User> users = userManager.searchUsers(searchString);
        if(users!=null && users.size()>0) {
            SearchPeopleEvent result = new SearchPeopleEvent(ResultEvent.STATUS_SUCCESS);
            result.setSuccessful(users);
            bus.post(result);
        }else{
            bus.post(new SearchPeopleEvent(ResultEvent.STATUS_INVALID));
            Timber.i("Users with nick or name as %s  not found in local database. Retrieving from the service...", searchString);
        }
    }

    @Override public void onAdded() {

    }

    @Override protected void onCancel() {
        /*no-op*/
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return RETRY_ATTEMPTS;
    }

    private boolean checkConnection() {
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        } else {
            return true;
        }
    }
}
