package gm.mobi.android.task.jobs.profile;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends BagdadBaseJob<UserInfoResultEvent> {

    private static final int PRIORITY = 3; //TODO definir valores estáticos para determinados casos
    private static final int RETRY_ATTEMPTS = 3;

    BagdadService service;

    UserManager userManager;
    FollowManager followManager;
    TeamManager teamManager;

    private Long userId;
    private UserEntity currentUser;
    private UserModelMapper userVOMapper;
    private NetworkUtil networkUtil;

    @Inject public GetUserInfoJob(Application application, Bus bus, SQLiteOpenHelper dbHelper, BagdadService service,
      NetworkUtil networkUtil1, UserManager userManager, FollowManager followManager, TeamManager teamManager, UserModelMapper userVOMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil1);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.teamManager = teamManager;
        this.networkUtil = networkUtil1;
        this.userVOMapper = userVOMapper;
        setOpenHelper(dbHelper);
    }

    public void init(Long userId, UserEntity currentUser) {
        this.userId = userId;
        this.currentUser = currentUser;
    }

    @Override public void run() throws SQLException, IOException {
        UserEntity userFromLocalDatabase = getUserFromDatabase();
        Long idCurrentUser = currentUser.getIdUser();
        FollowEntity follow = followManager.getFollowByUserIds(idCurrentUser,userId);
        UserModel userVO = null;
        if (userFromLocalDatabase != null) {
            userVO = userVOMapper.toUserModel(userFromLocalDatabase, follow, idCurrentUser);
            postSuccessfulEvent(new UserInfoResultEvent(userVO));
        } else {
            Timber.d("User with id %d not found in local database. Retrieving from the service...", userId);
        }

        if(networkUtil.isConnected(getContext())){
            UserEntity userFromService = getUserFromService();
            if(!idCurrentUser.equals(userId)){
                FollowEntity followFromService = getFolloFromService();
                if(followFromService.getIdUser()!=null) followManager.saveFollowFromServer(followFromService);
                follow = followManager.getFollowByUserIds(idCurrentUser,userId);
                userVO = userVOMapper.toUserModel(userFromService,follow,idCurrentUser);
            }

            postSuccessfulEvent(new UserInfoResultEvent(userVO));
            if (userFromLocalDatabase != null) {
                Timber.d("Obtained user from server found in database. Updating database.");
                userManager.saveUser(userFromService);
            }
        }


    }

    private FollowEntity getFolloFromService() throws IOException {
        return service.getFollowByIdUserFollowed(currentUser.getIdUser(), userId);
    }
    private UserEntity getUserFromDatabase() {
        return userManager.getUserByIdUser(userId);
    }

    private UserEntity getUserFromService() throws IOException {
        return service.getUserByIdUser(userId);
    }

    @Override
    protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        teamManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
