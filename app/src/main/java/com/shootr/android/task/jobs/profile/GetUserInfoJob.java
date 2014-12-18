package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.FollowEntity;
import com.shootr.android.domain.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
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

    private Long userId;
    private UserModelMapper userVOMapper;

    @Inject public GetUserInfoJob(Application application, Bus bus, ShootrService service, NetworkUtil networkUtil1,
      UserManager userManager, FollowManager followManager, SessionRepository sessionRepository, UserModelMapper userVOMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil1);
        this.service = service;
        this.userManager = userManager;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
        this.userVOMapper = userVOMapper;
    }

    public void init(Long userId) {
        this.userId = userId;
    }

    @Override public void run() throws SQLException, IOException {
        UserEntity userFromLocalDatabase = getUserFromDatabase();
        Long idCurrentUser = sessionRepository.getCurrentUserId();
        FollowEntity follow = followManager.getFollowByUserIds(idCurrentUser,userId);
        UserModel userVO = null;
        if (userFromLocalDatabase != null) {
            boolean isMe = idCurrentUser.equals(userFromLocalDatabase.getIdUser());
            userVO = userVOMapper.toUserModel(userFromLocalDatabase, follow, isMe);
            postSuccessfulEvent(new UserInfoResultEvent(userVO));
        } else {
            Timber.d("User with id %d not found in local database. Retrieving from the service...", userId);
        }

        if(hasInternetConnection()){
            UserEntity userFromService = getUserFromService();
            boolean isMe = idCurrentUser.equals(userId);
            FollowEntity followFromService = null;
            if(!isMe){
                followFromService = getFolloFromService();
                if(followFromService.getIdUser()!=null) {
                    followManager.saveFollowFromServer(followFromService);
                }
            }
            postSuccessfulEvent(new UserInfoResultEvent(userVOMapper.toUserModel(userFromService,followFromService,isMe)));
            if (userFromLocalDatabase != null) {
                Timber.d("Obtained user from server found in database. Updating database.");
                userManager.saveUser(userFromService);
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
        return service.getUserByIdUser(userId);
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

}
