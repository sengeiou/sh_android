package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleJob extends GetUsersFollowsJob {

    private final Application context;
    private final Bus bus;
    private final BagdadService service;
    private final NetworkUtil networkUtil;
    private Long userId;
    private UserManager userManager;
    private FollowManager followManager;

    @Inject public GetPeopleJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil, UserManager userManager, FollowManager followManager) {
        super(context, bus, service, networkUtil);
        this.context = context;
        this.bus = bus;
        this.service = service;
        this.networkUtil = networkUtil;
        this.userManager = userManager;
        this.followManager = followManager;
        userId = GolesApplication.get(context).getCurrentUser().getIdUser();
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<User> peopleFromDatabase = getPeopleFromDatabase();
        if (peopleFromDatabase != null && peopleFromDatabase.size() > 0) {
            sendSuccessfulResult(peopleFromDatabase);
        }
        // And refresh anyways
        super.run();
    }

    @Override @Deprecated
    public void init(Long userId, int followType) {
        super.init(userId, followType);
    }

    public void init() {
        super.init(userId, UserDtoFactory.GET_FOLLOWING);
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


}
