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
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class GetFollowUnfollowUserJob extends BagdadBaseJob<FollowUnFollowResultEvent>{

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;


    private User currentUser;
    private Long idUser;
    private int followUnfollowType;
    private int doIFollowHim;

    @Inject
    public GetFollowUnfollowUserJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
      BagdadService service, UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.setOpenHelper(openHelper);
    }

    public void init(User currentUser, Long idUser, int followUnfollowType){
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
                Follow followUser = followUser();
                doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                postSuccessfulEvent(new FollowUnFollowResultEvent(followUser, doIFollowHim));
            break;
            case UserDtoFactory.UNFOLLOW_TYPE:
                if(doIFollowHim == Follow.RELATIONSHIP_FOLLOWING){
                    Follow follow = unfollowUser();
                    doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                    postSuccessfulEvent(new FollowUnFollowResultEvent(follow,doIFollowHim));
                }else{
                    //TODO. Check if we aren't in the good followType
                    return;
                }
                break;
            }
        }


    public Follow unfollowUser() throws SQLException, IOException{
        Follow follow =  followManager.getFollowByUserIds(currentUser.getIdUser(), idUser);
        follow.setCsys_deleted(new Date());
        Follow followReceived = service.unfollowUser(follow);
        if(followReceived!=null){
            followManager.saveFollow(followReceived);
        }
        return followReceived;
    }

    public Follow followUser() throws IOException, SQLException{
        Follow follow = new Follow();
        follow.setFollowedUser(idUser);
        follow.setIdUser(currentUser.getIdUser());
        follow.setCsys_birth(new Date());
        follow.setCsys_modified(new Date());
        follow.setCsys_revision(0);
        Follow followReceived = service.followUser(follow);
        if(followReceived!=null){
            followManager.saveFollow(followReceived);
        }
        return followReceived;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
