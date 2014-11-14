package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class GetFollowUnfollowUserJob extends ShootrBaseJob<FollowUnFollowResultEvent> {

    private static final int PRIORITY = 6;

    ShootrService service;
    UserManager userManager;
    FollowManager followManager;

    private UserModelMapper userModelMapper;

    @Inject
    public GetFollowUnfollowUserJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
      ShootrService service, UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
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
        FollowEntity followEntity = null;
        if (!followsToUpdate.isEmpty()) {
            for (FollowEntity f : followsToUpdate) {
                if ("D".equals(f.getCsysSynchronized())) {
                    followEntity = unfollowUserAndRecordInDatabase(f);
                } else {
                    followEntity = followUserAndRecordInDatabase(f);
                }
            }
        }
        return userModelMapper.toUserModel(getUserFromDatabaseOrServer(followEntity.getFollowedUser()),followEntity,false);
    }


    public FollowEntity followUserAndRecordInDatabase(FollowEntity f) throws SQLException, IOException {
       FollowEntity followReceived = service.followUser(f);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }

    public UserEntity getUserFromDatabaseOrServer(Long idUser) throws SQLException, IOException {
       UserEntity userEntity = userManager.getUserByIdUser(idUser);
        if(hasInternetConnection() || userEntity == null){
            userEntity = service.getUserByIdUser(idUser);
            userManager.saveUser(userEntity);
        }
        return userEntity;
    }

    public FollowEntity unfollowUserAndRecordInDatabase(FollowEntity f) throws IOException, SQLException {
        FollowEntity followReceived = service.unfollowUser(f);
        if(followReceived!=null) {
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }


    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
