package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
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

public class GetFollowingsJob extends BagdadBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;

    private User currentUser;

    @Inject
    public GetFollowingsJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper, BagdadService service, UserManager userManager, FollowManager followManager, TeamManager teamManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
        this.setOpenHelper(openHelper);
    }

    public void init(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected void run() throws SQLException, IOException {
        List<User> followings = getFollowingsFromServer();
        Timber.d("Downloaded %d followings' users", followings.size());
        //TODO EXTERMINATE this useless call
        List<Follow> follows = getFollowsFromServer();

        // Save and send result
        userManager.saveUsers(followings);
        followManager.saveFollows(follows);
        postSuccessfulEvent(new FollowsResultEvent(followings));

    }

    private List<Follow> getFollowsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        return service.getFollows(currentUser.getIdUser(), modifiedFollows, UserDtoFactory.GET_FOLLOWING, true);
    }

    private List<User> getFollowingsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        List<User> following;
        following = service.getFollowings(currentUser.getIdUser(), modifiedFollows);
        return following;
    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
        teamManager.setDataBase(db);

    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

}
