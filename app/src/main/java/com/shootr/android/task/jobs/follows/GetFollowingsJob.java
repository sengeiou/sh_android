package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.FollowEntity;
import com.shootr.android.domain.UserEntity;
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

    private static final int PRIORITY = 10; //TODO Define next values for our queue

    ShootrService service;
    UserManager userManager;
    FollowManager followManager;
    @Inject UserModelMapper userModelMapper;
    private SessionRepository sessionRepository;

    @Inject
    public GetFollowingsJob(Application application, NetworkUtil networkUtil, Bus bus, ShootrService service,
      UserManager userManager, FollowManager followManager, SessionRepository sessionRepository) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
    }

    public List<FollowEntity> getFollowsByFollowing(List<UserEntity> following){
        List<FollowEntity> followsByFollowing = new ArrayList<>();
        for(UserEntity u:following){
            FollowEntity f = new FollowEntity();
            f.setIdUser(sessionRepository.getCurrentUserId());
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
            Long currentUserId = sessionRepository.getCurrentUserId();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId,user.getIdUser());
            boolean isMe = user.getIdUser().equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user, follow, isMe));
        }
        return userVOs;
    }

    private List<UserEntity> getFollowingsFromServer() throws IOException {
        Long modifiedFollows = followManager.getLastModifiedDate(DatabaseContract.FollowTable.TABLE);
        return service.getFollowing(sessionRepository.getCurrentUserId(), modifiedFollows);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
