package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public class GetUserInfoJob extends ShootrBaseJob<UserInfoResultEvent> {

    private static final int PRIORITY = 7; //TODO definir valores estáticos para determinados casos

    ShootrService service;

    UserManager userManager;
    FollowManager followManager;
    private SessionRepository sessionRepository;

    private String userId;
    private UserEntityModelMapper userVOMapper;
    private final UserEntityMapper userEntityMapper;
    private final UserApiService userApiService;

    @Inject public GetUserInfoJob(Application application,
      @Main Bus bus,
      ShootrService service,
      NetworkUtil networkUtil1,
      UserManager userManager,
      FollowManager followManager,
      SessionRepository sessionRepository,
      UserEntityModelMapper userVOMapper,
      UserEntityMapper userEntityMapper, UserApiService userApiService) {
        super(new Params(PRIORITY), application, bus, networkUtil1);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
        this.userVOMapper = userVOMapper;
        this.userEntityMapper = userEntityMapper;
        this.userApiService = userApiService;
    }

    public void init(String userId) {
        this.userId = userId;
    }

    @Override public void run() throws SQLException, IOException {
        UserEntity userFromLocalDatabase = getUserFromDatabase();
        String idCurrentUser = sessionRepository.getCurrentUserId();
        FollowEntity follow = followManager.getFollowByUserIds(idCurrentUser,userId);
        UserModel userVO = null;
        if (userFromLocalDatabase != null) {
            boolean isMe = idCurrentUser.equals(userFromLocalDatabase.getIdUser());
            userVO = userVOMapper.toUserModel(userFromLocalDatabase, follow, isMe);
            postSuccessfulEvent(new UserInfoResultEvent(userVO));
        } else {
            Timber.d("User with id %s not found in local database. Retrieving from the service...", userId);
        }

        if(hasInternetConnection()){
            UserEntity userFromService = getUserFromService();
            boolean isMe = idCurrentUser.equals(userId);
            FollowEntity followFromService = null;
            if(!isMe){
                followFromService = getFolloFromService();
                if(followFromService!=null && followFromService.getIdUser()!=null) {
                    followManager.saveFollowFromServer(followFromService);
                }
            }
            postSuccessfulEvent(new UserInfoResultEvent(userVOMapper.toUserModel(userFromService,followFromService,isMe)));
            if (userFromLocalDatabase != null) {
                Timber.d("Obtained user from server found in database. Updating database.");
                userManager.saveUser(userFromService);
                if (isMe) {
                    sessionRepository.setCurrentUser(userEntityMapper.transform(userFromService));
                }
            }
        }


    }

    private FollowEntity getFolloFromService() throws IOException {
        return service.getFollowByIdUserFollowed(sessionRepository.getCurrentUserId(), userId);
    }
    private UserEntity getUserFromDatabase() {
        return userManager.getUserByIdUser(userId);
    }

    private UserEntity getUserFromService() throws IOException {
        try {
            return userApiService.getUser(userId);
        } catch (ApiException e) {
            throw new IOException(e);
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
