package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUsersFollowsJob extends BagdadBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 5;

    BagdadService service;
    private Long idUserToRetrieveFollowsFrom;
    FollowManager followManager;
    private Integer followType;
    private User currentUser;

    @Inject public GetUsersFollowsJob(Application application, Bus bus, BagdadService service, NetworkUtil networkUtil, FollowManager followManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.followManager = followManager;
    }

    public void init(Long userId, Integer followType) {
        this.idUserToRetrieveFollowsFrom = userId;
        this.followType = followType;
        currentUser = GolesApplication.get(getContext()).getCurrentUser();
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> users = (followType.equals(UserDtoFactory.GET_FOLLOWERS)) ? getFollowerUsersFromService() : getFollowingUsersFromService();
        postSuccessfulEvent(new FollowsResultEvent(users));
    }

    private List<User> getFollowingUsersFromService() throws IOException {
        Timber.i("Hace la llamada de getFollowing");
        return service.getFollowing(idUserToRetrieveFollowsFrom, 0L);
    }
    private List<User> getFollowerUsersFromService() throws  IOException{
        Timber.i("Hace la llamada de getFollowers");
        return service.getFollowers(idUserToRetrieveFollowsFrom,0L);
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
