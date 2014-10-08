package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleLocalResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SearchPeopleLocalJob extends CancellableJob {

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;
    Application app;
    Bus bus;
    private SQLiteDatabase db;
    private UserManager userManager;
    private FollowManager followManager;
    private String searchString;

    @Inject
    public SearchPeopleLocalJob(Application app, Bus bus,
      UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY).groupBy(SearchPeopleRemoteJob.SEARCH_PEOPLE_GROUP));
        this.app = app;
        this.bus = bus;
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
        //At first we search in database
        List<User> results = retrieveDataFromDatabase();
        if (results != null) {
            SearchPeopleLocalResultEvent result = new SearchPeopleLocalResultEvent(ResultEvent.STATUS_SUCCESS);
            result.setSuccessful(results);
            bus.post(result);
        } else {
            bus.post(new SearchPeopleLocalResultEvent(ResultEvent.STATUS_INVALID));
        }
    }

    public List<User> retrieveDataFromDatabase(){
        return userManager.searchUsers(searchString);
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
}
