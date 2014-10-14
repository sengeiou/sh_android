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
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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


    public List<Follow> getFollowsByFollowing(List<User> following){
        List<Follow> followsByFollowing = new ArrayList<>();
        for(User u:following){
            Follow f = new Follow();
            f.setIdUser(currentUser.getIdUser());
            f.setFollowedUser(u.getIdUser());
            f.setCsys_birth(u.getCsys_birth());
            f.setCsys_modified(u.getCsys_modified());
            f.setCsys_revision(u.getCsys_revision());
            f.setCsys_deleted(u.getCsys_deleted());
            f.setCsys_synchronized(u.getCsys_synchronized());
            followsByFollowing.add(f);
        }
        return followsByFollowing;
    }

    @Override
    protected void run() throws SQLException, IOException {
        List<User> following = getFollowingsFromServer();
        Timber.d("Downloaded %d followings' users", following.size());
        List<Follow> followsByFollowing = getFollowsByFollowing(following);
        // Save and send result
        userManager.saveUsers(following);
        followManager.saveFollows(followsByFollowing);
        postSuccessfulEvent(new FollowsResultEvent(following));

    }

    private List<User> getFollowingsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        List<User> following;
        following = service.getFollowing(currentUser.getIdUser(), modifiedFollows);
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
