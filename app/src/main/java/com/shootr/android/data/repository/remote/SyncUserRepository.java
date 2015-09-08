package com.shootr.android.data.repository.remote;

import com.shootr.android.data.bus.Default;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.CachedSuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.CachedUserDataSource;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.SuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
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
    private final CachedUserDataSource cachedRemoteUserDataSource;
    private final CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource;
    private final FollowDataSource localFollowDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;
    private final SyncTrigger syncTrigger;
    private final Bus bus;

    @Inject public SyncUserRepository(@Local UserDataSource localUserDataSource,
      @Remote UserDataSource remoteUserDataSource, SessionRepository sessionRepository,
      @Remote SuggestedPeopleDataSource remoteSuggestedPeopleDataSource, CachedUserDataSource cachedRemoteUserDataSource,
      CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource, @Local FollowDataSource localFollowDataSource, UserEntityMapper userEntityMapper,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper, SyncableUserEntityFactory syncableUserEntityFactory,
      SyncTrigger syncTrigger, @Default Bus bus) {
        this.localUserDataSource = localUserDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
        this.sessionRepository = sessionRepository;
        this.remoteSuggestedPeopleDataSource = remoteSuggestedPeopleDataSource;
        this.cachedRemoteUserDataSource = cachedRemoteUserDataSource;
        this.cachedSuggestedPeopleDataSource = cachedSuggestedPeopleDataSource;
        this.localFollowDataSource = localFollowDataSource;
        this.userEntityMapper = userEntityMapper;
        this.suggestedPeopleEntityMapper = suggestedPeopleEntityMapper;
        this.syncableUserEntityFactory = syncableUserEntityFactory;
        this.syncTrigger = syncTrigger;
        this.bus = bus;
        this.bus.register(this);
    }

    @Override public List<User> getPeople() {
        List<UserEntity> remotePeopleEntities = cachedRemoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        markSynchronized(remotePeopleEntities);
        localFollowDataSource.putFollows(createFollowsFromUsers(remotePeopleEntities));
        return transformUserEntitiesForPeople(remotePeopleEntities);
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
        UserEntity user = cachedRemoteUserDataSource.getUser(id);
        return entityToDomain(user);
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
            localUserDataSource.putUser(remoteWatchEntity); //TODO Should think about making the cache responsible for this
            cachedRemoteUserDataSource.putUser(remoteWatchEntity);
            return userEntityMapper.transform(remoteWatchEntity, sessionRepository.getCurrentUserId());
        } catch (ServerCommunicationException e) {
            queueUpload(currentOrNewUserEntity, e);
            return userEntityMapper.transform(currentOrNewUserEntity, sessionRepository.getCurrentUserId());
        }
    }

    @Override public List<SuggestedPeople> getSuggestedPeople() {
        List<SuggestedPeopleEntity> suggestedPeopleEntities = remoteSuggestedPeopleDataSource.getSuggestedPeople(
          sessionRepository.getCurrentUserId());
        cachedSuggestedPeopleDataSource.putSuggestedPeople(suggestedPeopleEntities);
        return suggestedPeopleEntitiesToDomain(suggestedPeopleEntities);
    }

    @Override public List<User> getAllParticipants(String idStream, Long maxJoinDate) {
        List<UserEntity> allParticipants = remoteUserDataSource.getAllParticipants(idStream, maxJoinDate);
        List<User> participants = transformParticipantsEntities(allParticipants);
        return participants;
    }

    @Override public List<User> findParticipants(String idStream, String query) {
        List<UserEntity> allParticipants = remoteUserDataSource.findParticipants(idStream, query);
        List<User> participants = transformParticipantsEntities(allParticipants);
        return participants;
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

    private void prepareEntityForSynchronization(UserEntity userEntity) {
        if (!isReadyForSync(userEntity)) {
            userEntity.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        }
        localUserDataSource.putUser(userEntity);
    }

    private boolean isReadyForSync(UserEntity userEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(userEntity.getSynchronizedStatus()) || LocalSynchronized.SYNC_NEW.equals(
          userEntity.getSynchronizedStatus()) || LocalSynchronized.SYNC_DELETED.equals(userEntity.getSynchronizedStatus());
    }

    @Override public void dispatchSync() {
        List<UserEntity> notSynchronized = localUserDataSource.getEntitiesNotSynchronized();
        for (UserEntity userEntity : notSynchronized) {
            UserEntity synchedEntity = remoteUserDataSource.putUser(userEntity);
            synchedEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
            localUserDataSource.putUser(synchedEntity);
            Timber.d("Synchronized User entity: idUser=%s", userEntity.getIdUser());
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
        List<UserEntity> people = remoteUserDataSource.getFollowing(sessionRepository.getCurrentUserId());
        UserEntity me = remoteUserDataSource.getUser(sessionRepository.getCurrentUserId());
        // TODO download events? Do I need them? At least mine, I guess.
        markSynchronized(people);
        markEntitySynchronized(me);
        localUserDataSource.putUsers(people);
        localUserDataSource.putUser(me);
        cachedRemoteUserDataSource.resetCachedUpdateTime();
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
