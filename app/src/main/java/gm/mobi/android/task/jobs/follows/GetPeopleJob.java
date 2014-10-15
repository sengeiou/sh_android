package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleJob extends BagdadBaseJob<FollowsResultEvent> {

    public static final int PRIORITY = 5;
    BagdadService service;

    private Long currentUserId;
    private UserManager userManager;
    private FollowManager followManager;
    private UserVOMapper userVOMapper;


    @Inject public GetPeopleJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, SQLiteOpenHelper openHelper,UserManager userManager, FollowManager followManager, UserVOMapper userVOMapper) {
        super(new Params(PRIORITY),context,bus,networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.userVOMapper = userVOMapper;
        setOpenHelper(openHelper);
    }


    public void setService(BagdadService service) {
        this.service = service;
    }

    @Override
    protected void run() throws IOException, SQLException {

        List<User> peopleFromDatabase = getPeopleFromDatabase();

        List<UserVO> userVOs = getUserVOs(peopleFromDatabase);

        if (peopleFromDatabase != null && peopleFromDatabase.size() > 0) {
            postSuccessfulEvent(new FollowsResultEvent(userVOs));
        }

        List<User> peopleFromServer = service.getFollowing(currentUserId, 0L);
        Collections.sort(peopleFromServer, new NameComparator());
        userVOs = getUserVOs(peopleFromServer);
        postSuccessfulEvent(new FollowsResultEvent(userVOs));
    }

    public List<UserVO> getUserVOs(List<User> users){
        List<UserVO> userVOs = new ArrayList<>();
        for(User user: users){
            Follow follow = followManager.getFollowByUserIds(currentUserId, user.getIdUser());
            userVOs.add(userVOMapper.toVO(user,follow,currentUserId));
        }
        return userVOs;
    }

    public void init(long currentUserId) {
        this.currentUserId = currentUserId;
    }

    private List<User> getPeopleFromDatabase() throws SQLException {
        List<Long> usersFollowingIds = followManager.getUserFollowingIds(currentUserId);
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

