package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class GetUsersFollowsJob extends BagdadBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 5;

    BagdadService service;
    private Long idUserToRetrieveFollowsFrom;

    @Inject public GetUsersFollowsJob(Application application, Bus bus, BagdadService service, NetworkUtil networkUtil) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
    }

    public void init(Long userId) {
        this.idUserToRetrieveFollowsFrom = userId;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> users = getFollowsUsersFromService(idUserToRetrieveFollowsFrom, 0L);
        postSuccessfulEvent(new FollowsResultEvent(users));
    }

    private List<User> getFollowsUsersFromService(Long idUser, Long lastModifiedDate) throws IOException {
        return service.getFollowings(idUser, lastModifiedDate);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    @Override protected void createDatabase() {
        /* no-op */
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
    }


}
