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
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleJob extends BagdadBaseJob<FollowsResultEvent> {

    public static final int PRIORITY = 5;
    BagdadService service;

    private Long userId;
    private UserManager userManager;
    private FollowManager followManager;


    @Inject public GetPeopleJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, SQLiteOpenHelper openHelper,UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY),context,bus,networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        setOpenHelper(openHelper);
    }


    public void setService(BagdadService service) {
        this.service = service;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> peopleFromDatabase = getPeopleFromDatabase();
        if (peopleFromDatabase != null && peopleFromDatabase.size() > 0) {
            postSuccessfulEvent(new FollowsResultEvent(peopleFromDatabase));
        }

        List<User> peopleFromServer = service.getFollowing(userId, 0L);
        Collections.sort(peopleFromServer, new NameComparator());
        postSuccessfulEvent(new FollowsResultEvent(peopleFromServer));
    }

    public void init(long currentUserId) {
        userId = currentUserId;
    }

    private List<User> getPeopleFromDatabase() throws SQLException {
        List<Long> usersFollowingIds = followManager.getUserFollowingIds(userId);
        List<User> usersFollowing = userManager.getUsersByIds(usersFollowingIds);
        return usersFollowing;
    }

    @Override
    protected void createDatabase() {
        createReadableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    static class NameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getName().compareTo(user2.getName());
        }

    }
}

