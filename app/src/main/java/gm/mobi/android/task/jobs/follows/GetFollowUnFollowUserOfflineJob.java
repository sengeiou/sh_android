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
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class GetFollowUnFollowUserOfflineJob  extends BagdadBaseJob<FollowUnFollowResultEvent> {
    private static final int PRIORITY = 9; //TODO Define next values for our queue

    UserManager userManager;
    FollowManager followManager;

    private UserModelMapper userModelMapper;

    private UserEntity currentUser;
    private UserModel user;
    private Long idUser;
    private int followUnfollowType;
    private int doIFollowHim;

    @Inject
    public GetFollowUnFollowUserOfflineJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
       UserManager userManager, FollowManager followManager, UserModelMapper userModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userModelMapper = userModelMapper;
        this.setOpenHelper(openHelper);
    }

    public void init(UserEntity currentUser, Long idUser, int followUnfollowType){
        this.currentUser = currentUser;
        this.idUser = idUser;
        this.followUnfollowType = followUnfollowType;
    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        followManager.setDataBase(db);
        userManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        Long idCurrentUser = currentUser.getIdUser();
        doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
        switch (followUnfollowType){
            case UserDtoFactory.FOLLOW_TYPE:
                FollowEntity followUser = followUserInDB();
                UserEntity userToCreate = null;
                doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                if(followUser.getIdUser()!=null)
                 userToCreate = userModelMapper.userByUserVO(user);
                userManager.saveUser(userToCreate);
                user.setRelationship(doIFollowHim);
                postSuccessfulEvent(new FollowUnFollowResultEvent(user));
                break;
            case UserDtoFactory.UNFOLLOW_TYPE:
                if(doIFollowHim == FollowEntity.RELATIONSHIP_FOLLOWING){
                    FollowEntity follow = unfollowUserinDB();
                    doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                    user.setRelationship(doIFollowHim);
                    postSuccessfulEvent(new FollowUnFollowResultEvent(user));
                }else{
                    //TODO. Check if we aren't in the good followType
                    return;
                }
                break;
        }
    }


    public FollowEntity unfollowUserinDB() throws SQLException, IOException{
        //This case, It is not synchronized. It existed, and now we mark is going to be deleted, so We set synchronized
        //attribute to "U"
        FollowEntity follow =  followManager.getFollowByUserIds(currentUser.getIdUser(), idUser);
        follow.setCsys_deleted(new Date());
        follow.setCsys_synchronized("U");
        followManager.saveFollow(follow);
        return follow;
    }

    public FollowEntity followUserInDB() throws IOException, SQLException{
        //This case, It doesn't come from Server so, It isn't synchronized and probably It didn't exist in the past
        //So the syncrhonized attribute for this case is "N"
        FollowEntity follow = new FollowEntity();
        follow.setFollowedUser(idUser);
        follow.setIdUser(currentUser.getIdUser());
        follow.setCsys_birth(new Date());
        follow.setCsys_modified(new Date());
        follow.setCsys_revision(0);
        follow.setCsys_synchronized("N");
        followManager.saveFollow(follow);
        return follow;
    }



    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
