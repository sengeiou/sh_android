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
import java.util.List;
import javax.inject.Inject;

public class GetFollowUnfollowUserJob extends BagdadBaseJob<FollowUnFollowResultEvent>{

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;


    private User currentUser;
    private UserVO user;
    private Long idUser;
    private int followUnfollowType;
    private int doIFollowHim;
    private UserVOMapper userVOMapper;

    @Inject
    public GetFollowUnfollowUserJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper,
      BagdadService service, UserManager userManager, FollowManager followManager, UserVOMapper userVOMapper) {
        super(new Params(PRIORITY).requireNetwork(), application, bus, networkUtil);
        this.service = service;
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
        synchronized (followManager){
            checkIfWeHaveSomeChangesInFollowAndSendToServer();
            doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
            switch (followUnfollowType){
                case UserDtoFactory.FOLLOW_TYPE:
                    Follow followUser = followUser();
                    User userToCreate = null;
                    doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
                    if(followUser.getIdUser()!=null)
                        userToCreate = userVOMapper.userByUserVO(user);
                        userManager.saveUser(userToCreate);
                        user.setRelationship(doIFollowHim);
                        postSuccessfulEvent(new FollowUnFollowResultEvent(user));
                break;
                case UserDtoFactory.UNFOLLOW_TYPE:
                    if(doIFollowHim == Follow.RELATIONSHIP_FOLLOWING){
                        Follow follow = unfollowUser();
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
    }

    public void checkIfWeHaveSomeChangesInFollowAndSendToServer() throws IOException, SQLException{
       synchronized (followManager){
           List<Follow> followsToUpdate = followManager.getDatasForSendToServerInCase();
           Follow followReceived = null;
           if(followsToUpdate.size()>0){
               for(Follow f: followsToUpdate){
                   if(f.getCsys_deleted()!=null){
                       followReceived = service.unfollowUser(f);
                   }else{
                       followReceived = service.followUser(f);
                   }
                   if(followReceived!=null) followManager.saveFollowFromServer(followReceived);
               }
           }
       }

    }


    public Follow unfollowUser() throws SQLException, IOException{
        Follow follow =  followManager.getFollowByUserIds(currentUser.getIdUser(), idUser);
        follow.setCsys_deleted(new Date());
        Follow followReceived = service.unfollowUser(follow);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
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
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
