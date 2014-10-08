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
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleLocalResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleRemoteResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SearchPeopleRemoteJob extends CancellableJob {

    private static final int PRIORITY = 4;
    private static final int RETRY_ATTEMPTS = 3;

    public static final String SEARCH_PEOPLE_GROUP = "searchpeople";

    Application app;
    Bus bus;
    NetworkUtil networkUtil;
    BagdadService service;

    private String searchString;
    private int pageOffset;

    @Inject
    public SearchPeopleRemoteJob(Application app, Bus bus, BagdadService service, NetworkUtil networkUtil) {
        super(new Params(PRIORITY).groupBy(SEARCH_PEOPLE_GROUP));
        this.app = app;
        this.bus = bus;
        this.networkUtil = networkUtil;
        this.service = service;
    }

    public void init(String searchString, int pageOffset) {
        this.searchString = searchString;
        this.pageOffset = pageOffset;
    }

    @Override protected void createDatabase() {
    }

    @Override protected void setDatabaseToManagers() {
    }

    @Override protected void run() throws SQLException, IOException {
        if (!checkConnection()) {
            return;
        }

        try {
            PaginatedResult<List<User>> searchResults = getSearchFromServer();
            List<User> users = searchResults.getResult();
            if (users != null) {
                sendSuccess(searchResults);
            } else {
                bus.post(new SearchPeopleRemoteResultEvent(ResultEvent.STATUS_INVALID));
            }
        } catch (IOException e) {
            bus.post(new SearchPeopleRemoteResultEvent(ResultEvent.STATUS_SERVER_FAILURE).setServerError(e));
        }
    }

    private void sendSuccess(PaginatedResult<List<User>> result) {
        SearchPeopleRemoteResultEvent event = new SearchPeopleRemoteResultEvent(ResultEvent.STATUS_SUCCESS);
        event.setSuccessful(result);
        bus.post(event);
    }

    private PaginatedResult<List<User>> getSearchFromServer() throws IOException {
        return service.searchUsersByNameOrNickNamePaginated(searchString, pageOffset);
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
