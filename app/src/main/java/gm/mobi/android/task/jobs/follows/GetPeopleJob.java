package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleJob extends CancellableJob {

    public static final int PRIORITY = 5;
    Application context;
    Bus bus;
    BagdadService service;
    NetworkUtil networkUtil;
    private Long userId;
    private UserManager userManager;
    private FollowManager followManager;

    @Inject public GetPeopleJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY));
        this.context = context;
        this.bus = bus;
        this.service = service;
        this.networkUtil = networkUtil;
        this.userManager = userManager;
        this.followManager = followManager;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> peopleFromDatabase = getPeopleFromDatabase();
        if (peopleFromDatabase != null && peopleFromDatabase.size() > 0) {
            sendSuccessfulResult(peopleFromDatabase);
        }

        List<User> users = service.getFollowings(userId, 0l);
        Collections.sort(users,new NameComparator());
        sendSuccessfulResult(users);
     }

    public void init() {
        userId = GolesApplication.get(context).getCurrentUser().getIdUser();

    }



    protected void sendSuccessfulResult(List<User> followingUsers) {
        bus.post(new FollowsResultEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(followingUsers));
    }

    @Override
    protected void createDatabase() {
        db = createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers() {
        followManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    private List<User> getPeopleFromDatabase() throws SQLException {
        List<Long> usersFollowingIds = followManager.getUserFollowingIds(userId);
        List<User> usersFollowing = userManager.getUsersByIds(usersFollowingIds);
        return usersFollowing;
    }

    @Override public void onAdded() {

    }

    @Override protected void onCancel() {

    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }



    class NameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getName().compareTo(user2.getName());
        }

    }
}

