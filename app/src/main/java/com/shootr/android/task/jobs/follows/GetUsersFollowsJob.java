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
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
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

public class GetUsersFollowsJob extends ShootrBaseJob<FollowsResultEvent> {

    private static final int PRIORITY = 5;

    private final UserApiService userApiService;
    private String idUserToRetrieveFollowsFrom;
    FollowManager followManager;
    private Integer followType;
    private UserEntityModelMapper userModelMapper;
    private SessionRepository sessionRepository;

    @Inject public GetUsersFollowsJob(Application application,
      @Main Bus bus, NetworkUtil networkUtil,
      UserApiService userApiService,
      FollowManager followManager,
      UserEntityModelMapper userModelMapper,
      SessionRepository sessionRepository) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.userApiService = userApiService;
        this.userModelMapper = userModelMapper;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
    }

    public void init(String userId, Integer followType) {
        this.idUserToRetrieveFollowsFrom = userId;
        this.followType = followType;
    }

    @Override
    protected void run() throws IOException, SQLException {
        List<UserEntity> users = (followType.equals(UserDtoFactory.GET_FOLLOWERS)) ? getFollowerUsersFromService() : getFollowingUsersFromService();
        postSuccessfulEvent(new FollowsResultEvent(getUserVOs(users)));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity user: users){

            String idUser = user.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(sessionRepository.getCurrentUserId(), idUser);
            boolean isMe = idUser.equals(sessionRepository.getCurrentUserId());
            userVOs.add(userModelMapper.toUserModel(user,follow,isMe));
        }
        return userVOs;
    }

    private List<UserEntity> getFollowingUsersFromService() throws IOException {
        try {
            return userApiService.getFollowing(idUserToRetrieveFollowsFrom);
        } catch (ApiException e) {
            throw new IOException(e);
        }
    }
    private List<UserEntity> getFollowerUsersFromService() throws IOException {
        try {
            return userApiService.getFollowers(idUserToRetrieveFollowsFrom);
        } catch (ApiException e) {
            throw new IOException(e);
        }
    }



    @Override protected boolean isNetworkRequired() {
        return true;
    }

}
