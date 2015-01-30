package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncUserRepository implements UserRepository, SyncableRepository {

    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final UserDataSource remoteUserDataSource;
    private final UserEntityMapper userEntityMapper;

    @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource, SessionRepository sessionRepository,
      UserEntityMapper userEntityMapper) {
        this.localUserDataSource = localUserDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
        this.sessionRepository = sessionRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public List<User> getPeople() {
        List<UserEntity> remotePeopleEntities = remoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        //TODO store in local
        return transformUserEntitiesForPeople(remotePeopleEntities);
    }

    @Override public boolean isFollower(Long userId) {
        return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public boolean isFollowing(Long userId) {
        return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }

    private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
        List<User> userList = new ArrayList<>();
        long currentUserId = sessionRepository.getCurrentUserId();
        for (UserEntity localUserEntity : localUserEntities) {
            User user = userEntityMapper.transform(localUserEntity, currentUserId, isFollower(currentUserId),
              isFollowing(currentUserId));
            userList.add(user);
        }
        return userList;
    }
}
