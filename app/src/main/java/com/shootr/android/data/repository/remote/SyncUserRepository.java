package com.shootr.android.data.repository.remote;

import com.shootr.android.data.bus.Default;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.CachedSuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.SuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.remote.cache.UserCache;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.User;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncUserRepository implements UserRepository, SyncableRepository, WatchUpdateRequest.Receiver {

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

    @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource,
      SessionRepository sessionRepository,
      @Remote SuggestedPeopleDataSource remoteSuggestedPeopleDataSource,
      CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource,
      @Local FollowDataSource localFollowDataSource,
      UserEntityMapper userEntityMapper,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper,
      SyncableUserEntityFactory syncableUserEntityFactory,
      SyncTrigger syncTrigger,
      @Default Bus bus, UserCache userCache) {
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
        this.bus.register(this);
    }

    @Override public synchronized List<User> getPeople() {
        List<User> people = userCache.getPeople();
        if (people == null) {
            List<UserEntity> remotePeopleEntities =
              remoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
            savePeopleInLocal(remotePeopleEntities);
            people = transformUserEntitiesForPeople(remotePeopleEntities);
            userCache.putPeople(people);
        }
        return people;
    }

    private void savePeopleInLocal(List<UserEntity> remotePeople) {
        markSynchronized(remotePeople);
        localFollowDataSource.putFollows(createFollowsFromUsers(remotePeople));
        localUserDataSource.putUsers(remotePeople);
    }

    private List<FollowEntity> createFollowsFromUsers(List<UserEntity> following) {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<FollowEntity> followsByFollowing = new ArrayList<>();
        for (UserEntity u : following) {
            FollowEntity f = new FollowEntity();
            f.setIdUser(currentUserId);
            f.setFollowedUser(u.getIdUser());
            f.setBirth(u.getBirth());
            f.setModified(u.getModified());
            f.setRevision(u.getRevision());
            f.setDeleted(u.getDeleted());
            f.setSynchronizedStatus(u.getSynchronizedStatus());
            followsByFollowing.add(f);
        }
        return followsByFollowing;
    }

    @Override public User getUserById(String id) {
        UserEntity remoteUser = remoteUserDataSource.getUser(id);
        UserEntity localUser = localUserDataSource.getUser(id);
        if (localUser != null) {
            localUserDataSource.putUser(remoteUser);
        }
        return entityToDomain(remoteUser);
    }

    @Override
    public User getUserByUsername(String username){
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

    private List<SuggestedPeople> suggestedPeopleEntitiesToDomain(List<SuggestedPeopleEntity> suggestedPeopleEntities) {
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

    @Override public List<SuggestedPeople> getSuggestedPeople() {
        List<SuggestedPeopleEntity> suggestedPeopleEntities = remoteSuggestedPeopleDataSource.getSuggestedPeople();
        cachedSuggestedPeopleDataSource.putSuggestedPeople(suggestedPeopleEntities);
        return suggestedPeopleEntitiesToDomain(suggestedPeopleEntities);
    }

    @Override public List<User> getAllParticipants(String idStream, Long maxJoinDate) {
        List<UserEntity> allParticipants = remoteUserDataSource.getAllParticipants(idStream, maxJoinDate);
        return transformParticipantsEntities(allParticipants);
    }

    @Override public List<User> findParticipants(String idStream, String query) {
        List<UserEntity> allParticipants = remoteUserDataSource.findParticipants(idStream, query);
        return transformParticipantsEntities(allParticipants);
    }

    @Override
    public void updateWatch(User user) {
        //HEY: Esta entity no tiene por que estar bien actualizada ni nada. No se usa syncableEntityFactory porque modifica el synchronized normal, y no queremos eso. De todos modos solo se usaran los 3 campos del watch. Si se guarda el user entero puede dejar de funcionar bien.
        UserEntity entityWithWatchValues = userEntityMapper.transform(user);
        try {
            remoteUserDataSource.updateWatch(entityWithWatchValues);
            entityWithWatchValues.setWatchSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
            localUserDataSource.updateWatch(entityWithWatchValues);
        } catch (ServerCommunicationException e) {
            queueWatchUpload(entityWithWatchValues, e);
        }
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
        return LocalSynchronized.SYNC_UPDATED.equals(userEntity.getSynchronizedStatus()) || LocalSynchronized.SYNC_NEW.equals(
          userEntity.getSynchronizedStatus()) || LocalSynchronized.SYNC_DELETED.equals(userEntity.getSynchronizedStatus());
    }

    private boolean isWatchReadyForSync(UserEntity userEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(userEntity.getWatchSynchronizedStatus()) || LocalSynchronized.SYNC_NEW.equals(
          userEntity.getWatchSynchronizedStatus()) || LocalSynchronized.SYNC_DELETED.equals(userEntity.getWatchSynchronizedStatus());
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

    private void forceUpdatePeopleAndMe() {
        syncTrigger.triggerSync();
        userCache.invalidatePeople();
        this.getPeople();
        UserEntity me = remoteUserDataSource.getUser(sessionRepository.getCurrentUserId());
        markEntitySynchronized(me);
        localUserDataSource.putUser(me);
    }

    @Subscribe
    @Override public void onWatchUpdateRequest(WatchUpdateRequest.Event event) {
        try {
            forceUpdatePeopleAndMe();
        } catch (ServerCommunicationException networkError) {
            Timber.e(networkError, "Network error when updating data for a WatchUpdateRequest");
            /* swallow silently */
        }
    }
}
