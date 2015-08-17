package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetFollowingsJob extends ShootrBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 10; //TODO Define next values for our queue

    private final UserApiService userApiService;
    UserManager userManager;
    FollowManager followManager;
    @Inject UserEntityModelMapper userModelMapper;
    private SessionRepository sessionRepository;

    @Inject
    public GetFollowingsJob(Application application,
      NetworkUtil networkUtil,
      @Main Bus bus,
      UserApiService userApiService, UserManager userManager,
      FollowManager followManager,
      SessionRepository sessionRepository) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userApiService = userApiService;
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
            f.setBirth(u.getBirth());
            f.setModified(u.getModified());
            f.setRevision(u.getRevision());
            f.setDeleted(u.getDeleted());
            f.setSynchronizedStatus(u.getSynchronizedStatus());
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
        userManager.saveUsersAndDeleted(following);
        followManager.saveFollowsFromServer(followsByFollowing);
        List<UserModel> userFollows = getUserVOs(following);
        postSuccessfulEvent(new FollowsResultEvent(userFollows));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user:users){
            String currentUserId = sessionRepository.getCurrentUserId();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId,user.getIdUser());
            boolean isMe = user.getIdUser().equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(user, follow, isMe));
        }
        return userVOs;
    }

    private List<UserEntity> getFollowingsFromServer() throws IOException {
        try {
            return userApiService.getFollowing(sessionRepository.getCurrentUserId());
        } catch (ApiException e) {
            throw new IOException(e);
        }
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
