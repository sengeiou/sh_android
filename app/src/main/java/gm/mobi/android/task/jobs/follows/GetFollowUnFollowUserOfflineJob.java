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
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserVO;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class GetFollowUnFollowUserOfflineJob  extends BagdadBaseJob<FollowUnFollowResultEvent> {
    private static final int PRIORITY = 4; //TODO Define next values for our queue

    UserManager userManager;
    FollowManager followManager;

    private UserVOMapper userVOMapper;

    private User currentUser;
    private UserVO user;
    private Long idUser;
    private int followUnfollowType;
    private int doIFollowHim;

    @Inject
    public GetFollowUnFollowUserOfflineJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
       UserManager userManager, FollowManager followManager, UserVOMapper userVOMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userManager = userManager;
        this.followManager = followManager;
        this.userVOMapper = userVOMapper;
        this.setOpenHelper(openHelper);
    }

    public void init(User currentUser, UserVO user, int followUnfollowType){
        this.currentUser = currentUser;
        this.user = user;
        idUser = user.getIdUser();
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
        Long idUser = user.getIdUser();

        doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
        switch (followUnfollowType){
            case UserDtoFactory.FOLLOW_TYPE:
                Follow followUser = followUserInDB();
                User userToCreate = null;
                doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                if(followUser.getIdUser()!=null)
                    userToCreate = userVOMapper.userByUserVO(user);
                userManager.saveUser(userToCreate);
                postSuccessfulEvent(new FollowUnFollowResultEvent(followUser, doIFollowHim));
                break;
            case UserDtoFactory.UNFOLLOW_TYPE:
                if(doIFollowHim == Follow.RELATIONSHIP_FOLLOWING){
                    Follow follow = unfollowUserinDB();
                    doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                    postSuccessfulEvent(new FollowUnFollowResultEvent(follow,doIFollowHim));
                }else{
                    //TODO. Check if we aren't in the good followType
                    return;
                }
                break;
        }
    }


    public Follow unfollowUserinDB() throws SQLException, IOException{
        //This case, It is not synchronized. It existed, and now we mark is going to be deleted, so We set synchronized
        //attribute to "U"
        Follow follow =  followManager.getFollowByUserIds(currentUser.getIdUser(), idUser);
        follow.setCsys_deleted(new Date());
        follow.setCsys_synchronized("U");
        followManager.saveFollow(follow);
        return follow;
    }

    public Follow followUserInDB() throws IOException, SQLException{
        //This case, It doesn't come from Server so, It isn't synchronized and probably It didn't exist in the past
        //So the syncrhonized attribute for this case is "N"
        Follow follow = new Follow();
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
