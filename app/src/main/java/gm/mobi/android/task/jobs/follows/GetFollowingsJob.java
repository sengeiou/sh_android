package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowingsJob extends BagdadBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    BagdadService service;
    UserManager userManager;
    FollowManager followManager;
    @Inject UserModelMapper userModelMapper;

    private UserEntity currentUser;
    private boolean isMe;

    @Inject
    public GetFollowingsJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper, BagdadService service, UserManager userManager, FollowManager followManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.setOpenHelper(openHelper);
    }

    public void init(UserEntity currentUser) {
        this.currentUser = currentUser;
    }


    public List<FollowEntity> getFollowsByFollowing(List<UserEntity> following){
        List<FollowEntity> followsByFollowing = new ArrayList<>();
        for(UserEntity u:following){
            FollowEntity f = new FollowEntity();
            f.setIdUser(currentUser.getIdUser());
            f.setFollowedUser(u.getIdUser());
            f.setCsys_birth(u.getCsys_birth());
            f.setCsys_modified(u.getCsys_modified());
            f.setCsys_revision(u.getCsys_revision());
            f.setCsys_deleted(u.getCsys_deleted());
            f.setCsys_synchronized(u.getCsys_synchronized());
            followsByFollowing.add(f);
        }
        return followsByFollowing;
    }

    @Override
    protected void run() throws SQLException, IOException {
        List<UserEntity> following = getFollowingsFromServer();
        Timber.d("Downloaded %d followings' users", following.size());
        List<FollowEntity> followsByFollowing = getFollowsByFollowing(following);
        // Save and send result
        userManager.saveUsersFromServer(following);
        followManager.saveFollowsFromServer(followsByFollowing);
        List<UserModel> userFollows = getUserVOs(following);
        postSuccessfulEvent(new FollowsResultEvent(userFollows));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user:users){
            Long currentUserId = currentUser.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId,user.getIdUser());
            isMe = user.getIdUser().equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user, follow, isMe));
        }
        return userVOs;
    }



    private List<UserEntity> getFollowingsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(GMContract.FollowTable.TABLE);
        return service.getFollowing(currentUser.getIdUser(), modifiedFollows);
    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override
    protected void setDatabaseToManagers(SQLiteDatabase db) {
        userManager.setDataBase(db);
        followManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
