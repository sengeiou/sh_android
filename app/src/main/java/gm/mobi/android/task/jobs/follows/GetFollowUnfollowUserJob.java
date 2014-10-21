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


    private UserEntity currentUser;
    private UserModel user;
    private Long idUser;
    private int followUnfollowType;
    private int doIFollowHim;
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
         synchronized (followManager){
            checkIfWeHaveSomeChangesInFollowAndSendToServer();
            FollowEntity follow = followManager.getFollowByUserIds(idCurrentUser,idUser);
             FollowEntity followUser= null;
             doIFollowHim = followManager.doIFollowHimState(idCurrentUser, idUser);
             switch (followUnfollowType){
                case UserDtoFactory.FOLLOW_TYPE:
                     followUser = followUser();
                    user = userModelMapper.toUserModel(getUserByIdUser(idUser),followUser,idCurrentUser);
                    UserEntity userToCreate = null;
                    if(followUser.getIdUser()!=null)
                        userToCreate = userModelMapper.userByUserVO(user);
                        userManager.saveUser(userToCreate);
                        postSuccessfulEvent(new FollowUnFollowResultEvent(user));
                break;
                case UserDtoFactory.UNFOLLOW_TYPE:
                    if(doIFollowHim == FollowEntity.RELATIONSHIP_FOLLOWING){
                        followUser = unfollowUser();
                        user = userModelMapper.toUserModel(getUserByIdUser(idUser),followUser,idCurrentUser);
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
           List<FollowEntity> followsToUpdate = followManager.getDatasForSendToServerInCase();
           FollowEntity followReceived = null;
           if(followsToUpdate.size()>0){
               for(FollowEntity f: followsToUpdate){
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


    public UserEntity getUserByIdUser(Long idUser) throws IOException, SQLException {
        UserEntity user =  service.getUserByIdUser(idUser);
        if(user!=null){
            userManager.saveUser(user);
        }
        return user;
    }

    public FollowEntity unfollowUser() throws SQLException, IOException{
        FollowEntity follow =  followManager.getFollowByUserIds(currentUser.getIdUser(), idUser);
        follow.setCsys_deleted(new Date());
        FollowEntity followReceived = service.unfollowUser(follow);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }

    public FollowEntity followUser() throws IOException, SQLException{
        FollowEntity follow = new FollowEntity();
        follow.setFollowedUser(idUser);
        Timber.e("IDUSER WHO IS FOLLOWED"+String.valueOf(idUser));
        follow.setIdUser(currentUser.getIdUser());
        Timber.e("ID USER WHO FOLLOWS TO"+String.valueOf(currentUser.getIdUser()));
        follow.setCsys_birth(new Date());
        follow.setCsys_modified(new Date());        follow.setCsys_revision(0);
        FollowEntity followReceived = service.followUser(follow);
        if(followReceived!=null){
            followManager.saveFollowFromServer(followReceived);
        }
        return followReceived;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
