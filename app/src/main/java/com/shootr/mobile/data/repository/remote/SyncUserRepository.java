package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.bus.Default;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.data.entity.SynchroEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.SynchroDataSource;
import com.shootr.mobile.data.repository.datasource.user.CachedSuggestedPeopleDataSource;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.data.repository.datasource.user.SuggestedPeopleDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.data.repository.remote.cache.UserCache;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncUserRepository implements UserRepository, SyncableRepository, WatchUpdateRequest.Receiver {

    public static final int PAGE_SIZE = 100;
    public static final int PAGE = 0;
    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final UserDataSource remoteUserDataSource;
    private final SuggestedPeopleDataSource remoteSuggestedPeopleDataSource;
    private final CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource;
    private final FollowDataSource localFollowDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;
    private final SyncTrigger syncTrigger;
    private final Bus bus;
    private final UserCache userCache;
    private final FollowDataSource serviceFollowDataSource;
    private final SynchroDataSource synchroDataSource;
    private final AndroidTimeUtils androidTimeUtils;

  @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource, SessionRepository sessionRepository,
      @Remote SuggestedPeopleDataSource remoteSuggestedPeopleDataSource,
      CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource,
      @Local FollowDataSource localFollowDataSource, UserEntityMapper userEntityMapper,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper,
      SyncableUserEntityFactory syncableUserEntityFactory, SyncTrigger syncTrigger,
      @Default Bus bus, UserCache userCache, @Remote FollowDataSource serviceFollowDataSource,
      SynchroDataSource synchroDataSource, AndroidTimeUtils androidTimeUtils) {
    this.localUserDataSource = localUserDataSource;
    this.remoteUserDataSource = remoteUserDataSource;
    this.sessionRepository = sessionRepository;
    this.remoteSuggestedPeopleDataSource = remoteSuggestedPeopleDataSource;
    this.cachedSuggestedPeopleDataSource = cachedSuggestedPeopleDataSource;
    this.localFollowDataSource = localFollowDataSource;
    this.userEntityMapper = userEntityMapper;
    this.suggestedPeopleEntityMapper = suggestedPeopleEntityMapper;
    this.syncableUserEntityFactory = syncableUserEntityFactory;
    this.syncTrigger = syncTrigger;
    this.bus = bus;
    this.userCache = userCache;
    this.serviceFollowDataSource = serviceFollowDataSource;
    this.synchroDataSource = synchroDataSource;
    this.androidTimeUtils = androidTimeUtils;
    this.bus.register(this);
  }

  @Override public synchronized List<User> getPeople() {

    SynchroEntity synchroEntity = getSynchroEntity(SynchroDataSource.USER);
    Long userTimestamp = synchroDataSource.getTimestamp(SynchroDataSource.USER);
    try {
      List<UserEntity> remotePeopleEntities =
          remoteUserDataSource.getRelatedUsers(sessionRepository.getCurrentUserId(), userTimestamp);
      savePeopleInLocal(remotePeopleEntities);
      synchroDataSource.putEntity(synchroEntity);
    } catch (ServerCommunicationException networkError) {
      Timber.e(networkError, "Network error when updating data for a WatchUpdateRequest");
            /* swallow silently */
    }
    return transformUserEntitiesForPeople(
        localUserDataSource.getFollowing(sessionRepository.getCurrentUserId(), PAGE, PAGE_SIZE));
  }

  private SynchroEntity getSynchroEntity(String entity) {
    SynchroEntity synchroEntity = new SynchroEntity();
    synchroEntity.setEntity(entity);
    synchroEntity.setTimestamp(androidTimeUtils.getCurrentTime());
    return synchroEntity;
  }

  private void savePeopleInLocal(List<UserEntity> remotePeople) {
    markSynchronized(remotePeople);
    List<FollowEntity> follows = getFollows();
    localFollowDataSource.putFollows(follows);
    localUserDataSource.putUsers(remotePeople);
  }

  @NonNull private List<FollowEntity> getFollows() {
    SynchroEntity synchroEntity = getSynchroEntity(SynchroDataSource.FOLLOW);
    Long followTimestamp = synchroDataSource.getTimestamp(SynchroDataSource.FOLLOW);
    Integer page = 0;
    List<FollowEntity> follows =
        serviceFollowDataSource.getFollows(sessionRepository.getCurrentUserId(), page,
            followTimestamp);
    while (follows.size() == PAGE_SIZE) {
      localFollowDataSource.putFollows(follows);
      page++;
      follows = serviceFollowDataSource.getFollows(sessionRepository.getCurrentUserId(), page,
          followTimestamp);
    }
    synchroDataSource.putEntity(synchroEntity);
    return follows;
  }

    @Override public User getUserById(String id) {
        UserEntity remoteUser = remoteUserDataSource.getUser(id);
        localUserDataSource.putUser(remoteUser);
        return entityToDomain(remoteUser);
    }

    @Override public User getUserByUsername(String username) {
        UserEntity user = remoteUserDataSource.getUserByUsername(username);
        return entityToDomain(user);
    }

    private User entityToDomain(UserEntity remoteUser) {
        if (remoteUser == null) {
            return null;
        }
        return userEntityMapper.transform(remoteUser,
          sessionRepository.getCurrentUserId(),
          isFollower(remoteUser.getIdUser()),
          isFollowing(remoteUser.getIdUser()));
    }

    private SuggestedPeople suggestedPeopleEntityToDomain(SuggestedPeopleEntity remoteSuggestedPeopleEntity) {
        if (remoteSuggestedPeopleEntity == null) {
            return null;
        }
        return suggestedPeopleEntityMapper.transform(remoteSuggestedPeopleEntity);
    }

    private List<SuggestedPeople> suggestedPeopleEntitiesToDomain(
      List<SuggestedPeopleEntity> suggestedPeopleEntities) {
        List<SuggestedPeople> suggestedPeoples = new ArrayList<>(suggestedPeopleEntities.size());
        for (SuggestedPeopleEntity suggestedPeople : suggestedPeopleEntities) {
            suggestedPeoples.add(suggestedPeopleEntityToDomain(suggestedPeople));
        }
        return suggestedPeoples;
    }

    @Override public boolean isFollower(String userId) {
        return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public boolean isFollowing(String userId) {
        return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }

    @Override public User putUser(User user) {
        UserEntity currentOrNewUserEntity = syncableUserEntityFactory.updatedOrNewEntity(user);
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

    @Override public List<SuggestedPeople> getSuggestedPeople(String locale) {
        List<SuggestedPeopleEntity> suggestions = cachedSuggestedPeopleDataSource.getSuggestedPeople(locale);
        if (suggestions == null || suggestions.isEmpty()) {
            suggestions = remoteSuggestedPeopleDataSource.getSuggestedPeople(locale);
            cachedSuggestedPeopleDataSource.putSuggestedPeople(suggestions);
        }
        return suggestedPeopleEntitiesToDomain(suggestions);
    }

    @Override public List<User> getAllParticipants(String idStream, Long maxJoinDate) {
        List<UserEntity> allParticipants = remoteUserDataSource.getAllParticipants(idStream, maxJoinDate);
        return transformParticipantsEntities(allParticipants);
    }

    @Override public List<User> findParticipants(String idStream, String query) {
        List<UserEntity> allParticipants = remoteUserDataSource.findParticipants(idStream, query);
        return transformParticipantsEntities(allParticipants);
    }

    @Override public void updateWatch(User user) {
        UserEntity entityWithWatchValues = userEntityMapper.transform(user);
        try {
            remoteUserDataSource.updateWatch(entityWithWatchValues);
            entityWithWatchValues.setWatchSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
            localUserDataSource.updateWatch(entityWithWatchValues);
        } catch (ServerCommunicationException e) {
            queueWatchUpload(entityWithWatchValues, e);
        }
    }

    @Override public List<User> getFollowing(String idUser, Integer page, Integer pageSize) {
        return userEntityMapper.transformEntities(remoteUserDataSource.getFollowing(idUser, page, pageSize));
    }

    @Override public List<User> getFollowers(String idUser, Integer page, Integer pageSize) {
        return userEntityMapper.transformEntities(remoteUserDataSource.getFollowers(idUser, page, pageSize));
    }

    @Override public List<User> getLocalPeople(String idUser) {
        throw new IllegalArgumentException("No remote implementation");
    }

    @Override public User updateUserProfile(User updatedUserEntity)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        UserEntity currentOrNewUserEntity = syncableUserEntityFactory.updatedOrNewEntity(updatedUserEntity);
        try {
            UserEntity remoteWatchEntity = remoteUserDataSource.updateUser(currentOrNewUserEntity);
            markEntitySynchronized(remoteWatchEntity);
            localUserDataSource.putUser(remoteWatchEntity);
            return userEntityMapper.transform(remoteWatchEntity, sessionRepository.getCurrentUserId());
        } catch (ServerCommunicationException e) {
            queueUpload(currentOrNewUserEntity, e);
            return userEntityMapper.transform(currentOrNewUserEntity, sessionRepository.getCurrentUserId());
        }
    }

    @Override public List<User> findFriends(String searchString, Integer pageOffset, String locale)
      throws IOException {
        return transformUserEntitiesForPeople(remoteUserDataSource.findFriends(searchString, pageOffset, locale));
    }

    private List<User> transformParticipantsEntities(List<UserEntity> allParticipants) {
        List<User> participants = new ArrayList<>(allParticipants.size());
        for (UserEntity participantEntity : allParticipants) {
            User participant = userEntityMapper.transform(participantEntity,
              sessionRepository.getCurrentUserId(),
              isFollower(participantEntity.getIdUser()),
              isFollowing(participantEntity.getIdUser()));
            participants.add(participant);
        }
        return participants;
    }

    //region Synchronization
    private void queueUpload(UserEntity userEntity, ServerCommunicationException reason) {
        Timber.w(reason, "User upload queued: idUser %s", userEntity.getIdUser());
        prepareEntityForSynchronization(userEntity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void queueWatchUpload(UserEntity entity, ServerCommunicationException reason) {
        Timber.w(reason, "Watch upload queued: idUser %s", entity.getIdUser());
        entity.setWatchSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        localUserDataSource.updateWatch(entity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void prepareEntityForSynchronization(UserEntity userEntity) {
        if (!isEntityReadyForSync(userEntity)) {
            userEntity.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        }
        localUserDataSource.putUser(userEntity);
    }

    private boolean isEntityReadyForSync(UserEntity userEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(userEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_NEW.equals(userEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_DELETED.equals(userEntity.getSynchronizedStatus());
    }

    private boolean isWatchReadyForSync(UserEntity userEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(userEntity.getWatchSynchronizedStatus())
          || LocalSynchronized.SYNC_NEW.equals(userEntity.getWatchSynchronizedStatus())
          || LocalSynchronized.SYNC_DELETED.equals(userEntity.getWatchSynchronizedStatus());
    }

    @Override public void dispatchSync() {
        List<UserEntity> notSynchronized = localUserDataSource.getEntitiesNotSynchronized();
        for (UserEntity userEntity : notSynchronized) {
            if (isEntityReadyForSync(userEntity)) {
                UserEntity synchedEntity = remoteUserDataSource.putUser(userEntity);
                synchedEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
                localUserDataSource.putUser(synchedEntity);
                Timber.d("Synchronized User entity: idUser=%s", userEntity.getIdUser());
            }
            if (isWatchReadyForSync(userEntity)) {
                remoteUserDataSource.updateWatch(userEntity);
                userEntity.setWatchSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
                localUserDataSource.updateWatch(userEntity);
            }
        }
    }
    //endregion

    private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
        return transformParticipantsEntities(localUserEntities);
    }

    private void markSynchronized(List<UserEntity> peopleEntities) {
        for (UserEntity userEntity : peopleEntities) {
            markEntitySynchronized(userEntity);
        }
    }

    private void markEntitySynchronized(UserEntity userEntity) {
        userEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    }

    @Override public void forceUpdatePeople() {
        syncTrigger.triggerSync();
        userCache.invalidatePeople();
        this.getPeople();
    }

    @Subscribe @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        try {
            //TODO test if calll this method is necesary forceUpdatePeopleAndMe();
        } catch (ServerCommunicationException networkError) {
            Timber.e(networkError, "Network error when updating data for a WatchUpdateRequest");
            /* swallow silently */
        }
    }
}
