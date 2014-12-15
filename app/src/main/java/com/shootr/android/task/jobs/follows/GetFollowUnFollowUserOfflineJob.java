package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class GetFollowUnFollowUserOfflineJob  extends ShootrBaseJob<FollowUnFollowResultEvent> {
    private static final int PRIORITY = 9; //TODO Define next values for our queue

    private final UserManager userManager;
    private final FollowManager followManager;
    private final UserModelMapper userModelMapper;
    private final SessionManager sessionManager;

    private Long idUser;
    private int followUnfollowType;

    @Inject
    public GetFollowUnFollowUserOfflineJob(Application application, NetworkUtil networkUtil, Bus bus, UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper,
      SessionManager sessionManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.sessionManager = sessionManager;
    }

    public void init(Long idUser, int followUnfollowType){
        this.idUser = idUser;
        this.followUnfollowType = followUnfollowType;
    }

    @Override protected void run() throws SQLException, IOException {
        Long idCurrentUser = sessionManager.getCurrentUserId();
        int doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
        UserModel userToReturn = null;
        switch (followUnfollowType){
            case UserDtoFactory.FOLLOW_TYPE:
                doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                if(doIFollowHim == FollowEntity.RELATIONSHIP_NONE){
                    FollowEntity followUser = followUserInDB();
                    if(followUser.getIdUser()!=null){
                        UserEntity user = userManager.getUserByIdUser(idUser);
                        if(user!=null){
                            userManager.saveUser(user);
                            userToReturn = userModelMapper.toUserModel(user,followUser, false);
                        }
                        postSuccessfulEvent(new FollowUnFollowResultEvent(userToReturn));
                    }

                }

                break;
            case UserDtoFactory.UNFOLLOW_TYPE:
                if(doIFollowHim == FollowEntity.RELATIONSHIP_FOLLOWING){
                    FollowEntity follow = unfollowUserinDB();
                    UserEntity userEntity = userManager.getUserByIdUser(idUser);
                    if(userEntity!=null){
                        userManager.saveUser(userEntity);
                        userToReturn = userModelMapper.toUserModel(userEntity,follow, false);
                        postSuccessfulEvent(new FollowUnFollowResultEvent(userToReturn));
                    }

                }else{
                    //TODO. Check if we aren't in the good followType
                    return;
                }
                break;
            default:
                break;

        }
    }

    public FollowEntity unfollowUserinDB() throws SQLException, IOException{
        //This case, It is not synchronized. It existed, and now we mark is going to be deleted, so We set synchronized
        //attribute to "U"
        FollowEntity follow =  followManager.getFollowByUserIds(sessionManager.getCurrentUserId(), idUser);
        follow.setCsysDeleted(new Date());
        follow.setCsysSynchronized("D");
        followManager.saveFollow(follow);

        userManager.saveUser(sessionManager.getCurrentUser());
        return follow;
    }

    public FollowEntity followUserInDB() throws IOException, SQLException{
        //This case, It doesn't come from Server so, It isn't synchronized and probably It didn't exist in the past
        //So the syncrhonized attribute for this case is "N"
        Long idCurrentUser = sessionManager.getCurrentUserId();
         FollowEntity follow = followManager.getFollowByUserIds(sessionManager.getCurrentUserId(),idUser);
        if(follow!=null && ("N".equals(follow.getCsysSynchronized()) || "U".equals(follow.getCsysSynchronized()) || "D".equals(follow.getCsysSynchronized()))){
            follow.setCsysSynchronized("U");
        }else{
            follow = new FollowEntity();
            follow.setCsysSynchronized("N");
        }
        follow.setIdUser(idCurrentUser);
        follow.setFollowedUser(idUser);
        follow.setCsysBirth(new Date());
        follow.setCsysModified(new Date());
        follow.setCsysRevision(0);
        followManager.saveFollow(follow);
        userManager.saveUser(sessionManager.getCurrentUser());
        return follow;
    }


    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
