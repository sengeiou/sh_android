package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.PaginatedResult;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.SearchPeopleRemoteResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleRemoteJob extends BagdadBaseJob<PaginatedResult<List<User>>> {

    private static final int PRIORITY = 4;
    public static final String SEARCH_PEOPLE_GROUP = "searchpeople";

    BagdadService service;

    private String searchString;
    private int pageOffset;

    @Inject
    public SearchPeopleRemoteJob(Application app, Bus bus, BagdadService service, NetworkUtil networkUtil) {
        super(new Params(PRIORITY).groupBy(SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.service = service;
    }

    public void init(String searchString, int pageOffset) {
        this.searchString = searchString;
        this.pageOffset = pageOffset;
    }

    @Override protected void run() throws SQLException, IOException {
        PaginatedResult<List<User>> searchResults = getSearchFromServer();
        postSuccessfulEvent(searchResults);
    }

    private PaginatedResult<List<User>> getSearchFromServer() throws IOException {
        return service.searchUsersByNameOrNickNamePaginated(searchString, pageOffset);
    }

    @Override protected void createDatabase() {
        /* no-op */
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
