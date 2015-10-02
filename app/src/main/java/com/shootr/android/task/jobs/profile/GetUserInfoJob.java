package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
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

    UserManager userManager;
    FollowManager followManager;
    private SessionRepository sessionRepository;

    private String userId;
    private UserEntityModelMapper userVOMapper;
    private final UserEntityMapper userEntityMapper;
    private final UserDataSource remoteUserDataSource;

    @Inject public GetUserInfoJob(Application application,
      @Main Bus bus, NetworkUtil networkUtil1,
      UserManager userManager,
      FollowManager followManager,
      SessionRepository sessionRepository,
      UserEntityModelMapper userVOMapper,
      UserEntityMapper userEntityMapper,
      @Remote UserDataSource remoteUserDataSource) {
        super(new Params(PRIORITY), application, bus, networkUtil1);
        this.userManager = userManager;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
        this.userVOMapper = userVOMapper;
        this.userEntityMapper = userEntityMapper;
        this.remoteUserDataSource = remoteUserDataSource;
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
            postSuccessfulEvent(new UserInfoResultEvent(userVOMapper.toUserModel(userFromService,follow,isMe)));
            userManager.saveUser(userFromService);
            if (isMe) {
                sessionRepository.setCurrentUser(userEntityMapper.transform(userFromService));
            }
        }


    }

    private UserEntity getUserFromDatabase() {
        return userManager.getUserByIdUser(userId);
    }

    private UserEntity getUserFromService() throws IOException {
        try {
            return remoteUserDataSource.getUser(userId);
        } catch (ServerCommunicationException e) {
            throw new IOException(e);
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
