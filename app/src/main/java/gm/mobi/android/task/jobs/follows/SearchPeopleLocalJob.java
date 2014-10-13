package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.follows.SearchPeopleLocalResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleLocalJob extends BagdadBaseJob<SearchPeopleLocalResultEvent> {

    private static final int PRIORITY = 4;

    private UserManager userManager;
    private FollowManager followManager;

    private String searchString;

    @Inject
    public SearchPeopleLocalJob(Application app, Bus bus, NetworkUtil networkUtil, SQLiteOpenHelper openHelper,
      UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY).groupBy(SearchPeopleRemoteJob.SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        setOpenHelper(openHelper);
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
        postSuccessfulEvent(new SearchPeopleLocalResultEvent(results));
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public List<User> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
    }

}
