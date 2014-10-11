package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.jobs.BagdadBaseJob;

public class SearchPeopleLocalJob extends BagdadBaseJob<List<User>> {

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;

    private UserManager userManager;
    private FollowManager followManager;

    private String searchString;

    @Inject
    public SearchPeopleLocalJob(Application app, Bus bus, NetworkUtil networkUtil,
      UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY).groupBy(SearchPeopleRemoteJob.SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
    }

    public void init(String searchString) {
        this.searchString = searchString;
    }

    @Override protected void createDatabase() {
        createReadableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        //At first we search in database
        List<User> results = retrieveDataFromDatabase();
        postSuccessfulEvent(results);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public List<User> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
    }

}
