package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.RepositoryException;
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
    private final FollowDataSource localFollowDataSource;
    private final UserEntityMapper userEntityMapper;

    @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource, SessionRepository sessionRepository,
      @Local FollowDataSource localFollowDataSource, UserEntityMapper userEntityMapper) {
        this.localUserDataSource = localUserDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
        this.sessionRepository = sessionRepository;
        this.localFollowDataSource = localFollowDataSource;
        this.userEntityMapper = userEntityMapper;
    }

    @Override public List<User> getPeople() {
        List<UserEntity> remotePeopleEntities = remoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        markSynchronized(remotePeopleEntities);
        localUserDataSource.putUsers(remotePeopleEntities);
        localFollowDataSource.putFollows(getFollowsByFollowingUsers(remotePeopleEntities));
        return transformUserEntitiesForPeople(remotePeopleEntities);
    }

    public List<FollowEntity> getFollowsByFollowingUsers(List<UserEntity> following){
        long currentUserId = sessionRepository.getCurrentUserId();
        List<FollowEntity> followsByFollowing = new ArrayList<>();
        for(UserEntity u:following){
            FollowEntity f = new FollowEntity();
            f.setIdUser(currentUserId);
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

    @Override public User getUserById(Long id) {
        UserEntity localUser = localUserDataSource.getUser(id);
        if (localUser != null) {
            //TODO update always? or when?
            return entityToDomain(localUser);
        } else {
            UserEntity remoteUser = remoteUserDataSource.getUser(id);
            if (remoteUser != null) {
                localUserDataSource.putUser(remoteUser);
                return entityToDomain(remoteUser);
            } else {
                return null;
            }
        }
    }

    private User entityToDomain(UserEntity remoteUser) {
        return userEntityMapper.transform(remoteUser, sessionRepository.getCurrentUserId(), isFollower(remoteUser.getIdUser()), isFollowing(remoteUser.getIdUser()));
    }

    @Override public List<User> getUsersByIds(List<Long> userIds) {
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = getUserById(userId);
            if (user == null) {
                throw new RepositoryException("User with id " + userId + " not found");
            }
            users.add(user);
        }
        return users;
    }

    @Override public boolean isFollower(Long userId) {
        return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public boolean isFollowing(Long userId) {
        return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public User putUser(User user) {
        remoteUserDataSource.putUser(userEntityMapper.transform(user));
        //TODO transform server's user and return it instead
        return user;
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

    private void markSynchronized(List<UserEntity> peopleEntities) {
        for (UserEntity userEntity : peopleEntities) {
            userEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
        }
    }
}
