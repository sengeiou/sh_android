package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class GetUnfollowUserJob extends BagdadBaseJob<FollowUnFollowResultEvent> {
    private static final int PRIORITY = 6;
    BagdadService service;
    UserManager userManager;
    FollowManager followManager;


    private User currentUser;
    private Long idUser;

    @Inject
    public GetUnfollowUserJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper, BagdadService service, UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.setOpenHelper(openHelper);
    }

    public void init(User currentUser, Long idUser){
        this.currentUser = currentUser;
        this.idUser = idUser;
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
        if(followManager.doIFollowHimRelationship(idCurrentUser, idUser)){
            Follow follow = unfollowUser();
            boolean doIfollowHim = followManager.doIFollowHimRelationship(idCurrentUser,idUser);
            postSuccessfulEvent(new FollowUnFollowResultEvent(follow,doIfollowHim));
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
    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
