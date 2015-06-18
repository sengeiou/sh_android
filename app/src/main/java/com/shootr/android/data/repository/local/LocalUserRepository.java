package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LocalUserRepository implements UserRepository {

    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;

    @Inject public LocalUserRepository(SessionRepository sessionRepository, @Local UserDataSource userDataSource,
      UserEntityMapper userEntityMapper, SyncableUserEntityFactory syncableUserEntityFactory) {
        this.sessionRepository = sessionRepository;
        localUserDataSource = userDataSource;
        this.userEntityMapper = userEntityMapper;
        this.syncableUserEntityFactory = syncableUserEntityFactory;
    }

    @Override public List<User> getPeople() {
        List<UserEntity> userEntities = localUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        return transformUserEntitiesForPeople(userEntities);
    }

    @Override public User getUserById(String id) {
        return userEntityMapper.transform(localUserDataSource.getUser(id),
          sessionRepository.getCurrentUserId(),
          isFollower(id),
          isFollowing(id));
    }

    @Override
    public User getUserByUsername(String username){
        return userEntityMapper.transform(localUserDataSource.getUserByUsername(username));
    }

    @Override public List<User> getUsersByIds(List<String> ids) {
        // TODO optimize with its own query
        List<User> users = new ArrayList<>(ids.size());
        for (String id : ids) {
            users.add(getUserById(id));
        }
        return users;
    }

    @Override public boolean isFollower(String userId) {
        return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public boolean isFollowing(String userId) {
        return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public User putUser(User user) {
        UserEntity currentOrNewEntity = syncableUserEntityFactory.updatedOrNewEntity(user);
        localUserDataSource.putUser(currentOrNewEntity);
        return user;
    }

    private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
        List<User> userList = new ArrayList<>();
        String currentUserId = sessionRepository.getCurrentUserId();
        for (UserEntity localUserEntity : localUserEntities) {
            User user = userEntityMapper.transform(localUserEntity, currentUserId, isFollower(currentUserId), isFollowing(currentUserId));
            userList.add(user);
        }
        return userList;
    }
}
