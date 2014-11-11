package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowingsJob extends ShootrBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 6; //TODO Define next values for our queue

    ShootrService service;
    UserManager userManager;
    FollowManager followManager;
    @Inject UserModelMapper userModelMapper;

    private UserEntity currentUser;
    private boolean isMe;

    @Inject
    public GetFollowingsJob(Application application, NetworkUtil networkUtil, Bus bus, SQLiteOpenHelper openHelper, ShootrService service, UserManager userManager, FollowManager followManager) {
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
            f.setCsysBirth(u.getCsysBirth());
            f.setCsysModified(u.getCsysModified());
            f.setCsysRevision(u.getCsysRevision());
            f.setCsysDeleted(u.getCsysDeleted());
            f.setCsysSynchronized(u.getCsysSynchronized());
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
        Long modifiedFollows = followManager.getLastModifiedDate(DatabaseContract.FollowTable.TABLE);
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
