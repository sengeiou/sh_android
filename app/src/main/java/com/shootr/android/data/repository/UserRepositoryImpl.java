package com.shootr.android.data.repository;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserRepositoryImpl implements UserRepository{

    private final UserManager userManager;
    private final FollowManager followManager;
    private final ShootrService service;
    private final SessionRepository sessionRepository;
    private final UserEntityMapper userEntityMapper;

    @Inject public UserRepositoryImpl(UserManager userManager, FollowManager followManager, ShootrService service,
      SessionRepository sessionRepository, UserEntityMapper userEntityMapper) {
        this.userManager = userManager;
        this.followManager = followManager;
        this.service = service;
        this.sessionRepository = sessionRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public void getPeople(UserListCallback callback) {
        List<User> localUsers = peopleFromDatabase();
        callback.onLoaded(localUsers);

        List<User> remoteUsers = peopleFromServer();
        callback.onLoaded(remoteUsers);
    }

    private List<User> peopleFromDatabase() {
        try {
            List<Long> usersFollowingIds = followManager.getUserFollowingIds(sessionRepository.getCurrentUserId());
            List<UserEntity> usersFollowing = userManager.getUsersByIds(usersFollowingIds);
            return transformUserEntities(usersFollowing);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private List<User> peopleFromServer() {
        try {
            List<UserEntity> peopleFromServer = service.getFollowing(sessionRepository.getCurrentUserId(), 0L);
            return transformUserEntities(peopleFromServer);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    private List<User> transformUserEntities(List<UserEntity> localUserEntities) {
        List<User> userList = new ArrayList<>();
        long currentUserId = sessionRepository.getCurrentUserId();
        for (UserEntity localUserEntity : localUserEntities) {
            User user = userEntityMapper.transform(localUserEntity, currentUserId, isFollower(currentUserId), isFollowing(currentUserId));
            userList.add(user);
        }
        return userList;
    }

    @Override public boolean isFollower(Long userId) {
        FollowEntity follow = followManager.getFollowByUserIds(userId, sessionRepository.getCurrentUserId());
        return follow != null;
    }

    @Override public boolean isFollowing(Long userId) {
        FollowEntity follow = followManager.getFollowByUserIds(sessionRepository.getCurrentUserId(), userId);
        return follow != null;
    }


}
