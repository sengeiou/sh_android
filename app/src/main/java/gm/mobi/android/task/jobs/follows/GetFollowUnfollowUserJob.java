package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowUnfollowUserJob extends BagdadBaseJob<FollowUnFollowResultEvent>{

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;

    private UserModelMapper userModelMapper;

    @Inject
    public GetFollowUnfollowUserJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
      BagdadService service, UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY).requireNetwork(), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.setOpenHelper(openHelper);
    }

    @Override protected void createDatabase() {
            createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
         synchronized (followManager) {
             UserModel user = checkIfWeHaveSomeChangesInFollowAndSendToServer();
             postSuccessfulEvent(new FollowUnFollowResultEvent(user));
         }
    }

    public UserModel checkIfWeHaveSomeChangesInFollowAndSendToServer() throws IOException, SQLException {
        List<FollowEntity> followsToUpdate = followManager.getDatasForSendToServerInCase();
        UserModel resUserModel = null;
        if (followsToUpdate.size() > 0) {
            for (FollowEntity f : followsToUpdate) {
                if (f.getCsys_synchronized().equals("D")) {
                    unfollowUserAndRecordInDatabase(f);
                } else {
                    resUserModel = followUserAndRecordInDatabase(f, getUserFromDatabaseOrServer(f.getFollowedUser()));
                }
            }
        }
        return resUserModel;
    }


    public UserModel followUserAndRecordInDatabase(FollowEntity f, UserEntity userEntity) throws SQLException, IOException {
       FollowEntity followReceived = service.followUser(f);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
            return userModelMapper.toUserModel(userEntity,followReceived, false);
        }
        return null;
    }

    public UserEntity getUserFromDatabaseOrServer(Long idUser) throws SQLException, IOException {
       UserEntity userEntity = userManager.getUserByIdUser(idUser);
        if(userEntity==null){
            userEntity = service.getUserByIdUser(idUser);
            userManager.saveUser(userEntity);
        }
        return userEntity;
    }

    public void unfollowUserAndRecordInDatabase(FollowEntity f) throws IOException, SQLException {
        FollowEntity followReceived = service.unfollowUser(f);
        if(followReceived!=null) followManager.saveFollowFromServer(followReceived);
    }


    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
