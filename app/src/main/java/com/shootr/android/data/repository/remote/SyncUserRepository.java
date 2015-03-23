package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncUserRepository implements UserRepository, SyncableRepository {

    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final UserDataSource remoteUserDataSource;
    private final FollowDataSource localFollowDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;
    private final SyncTrigger syncTrigger;

    @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource, SessionRepository sessionRepository,
      @Local FollowDataSource localFollowDataSource, UserEntityMapper userEntityMapper,
      SyncableUserEntityFactory syncableUserEntityFactory, SyncTrigger syncTrigger) {
        this.localUserDataSource = localUserDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
        this.sessionRepository = sessionRepository;
        this.localFollowDataSource = localFollowDataSource;
        this.userEntityMapper = userEntityMapper;
        this.syncableUserEntityFactory = syncableUserEntityFactory;
        this.syncTrigger = syncTrigger;
    }

    @Override public List<User> getPeople() {
        List<UserEntity> remotePeopleEntities = remoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        markSynchronized(remotePeopleEntities);
        localUserDataSource.putUsers(remotePeopleEntities);
        localFollowDataSource.putFollows(getFollowsByFollowingUsers(remotePeopleEntities));
        return transformUserEntitiesForPeople(remotePeopleEntities);
    }

    public List<FollowEntity> getFollowsByFollowingUsers(List<UserEntity> following) {
        long currentUserId = sessionRepository.getCurrentUserId();
        List<FollowEntity> followsByFollowing = new ArrayList<>();
        for (UserEntity u : following) {
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
            //TODO update always? or when? cache maybe?
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
        return userEntityMapper.transform(remoteUser,
          sessionRepository.getCurrentUserId(),
          isFollower(remoteUser.getIdUser()),
          isFollowing(remoteUser.getIdUser()));
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
        UserEntity currentOrNewUserEntity = syncableUserEntityFactory.currentOrNewEntity(user);
        try {
            UserEntity remoteWatchEntity = remoteUserDataSource.putUser(currentOrNewUserEntity);
            markEntitySynchronized(remoteWatchEntity);
            localUserDataSource.putUser(remoteWatchEntity);
            return userEntityMapper.transform(remoteWatchEntity, sessionRepository.getCurrentUserId());
        } catch (ServerCommunicationException e) {
            queueUpload(currentOrNewUserEntity, e);
            return userEntityMapper.transform(currentOrNewUserEntity, sessionRepository.getCurrentUserId());
        }
    }

    //region Synchronization
    private void queueUpload(UserEntity userEntity, ServerCommunicationException reason) {
        Timber.w(reason, "User upload queued: idUser %d", userEntity.getIdUser());
        prepareEntityForSynchronization(userEntity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void prepareEntityForSynchronization(UserEntity userEntity) {
        if (!isReadyForSync(userEntity)) {
            userEntity.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        }
        localUserDataSource.putUser(userEntity);
    }

    private boolean isReadyForSync(UserEntity userEntity) {
        return Synchronized.SYNC_UPDATED.equals(userEntity.getCsysSynchronized()) || Synchronized.SYNC_NEW.equals(
          userEntity.getCsysSynchronized()) || Synchronized.SYNC_DELETED.equals(userEntity.getCsysSynchronized());
    }

    @Override public void dispatchSync() {
        List<UserEntity> notSynchronized = localUserDataSource.getEntitiesNotSynchronized();
        for (UserEntity userEntity : notSynchronized) {
            UserEntity synchedEntity = remoteUserDataSource.putUser(userEntity);
            synchedEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
            localUserDataSource.putUser(synchedEntity);
            Timber.d("Synchronized User entity: idUser=%d", userEntity.getIdUser());
        }
    }
    //endregion

    private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
        List<User> userList = new ArrayList<>();
        long currentUserId = sessionRepository.getCurrentUserId();
        for (UserEntity localUserEntity : localUserEntities) {
            User user = userEntityMapper.transform(localUserEntity,
              currentUserId,
              isFollower(currentUserId),
              isFollowing(currentUserId));
            userList.add(user);
        }
        return userList;
    }

    private void markSynchronized(List<UserEntity> peopleEntities) {
        for (UserEntity userEntity : peopleEntities) {
            markEntitySynchronized(userEntity);
        }
    }

    private void markEntitySynchronized(UserEntity userEntity) {
        userEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
    }
}
