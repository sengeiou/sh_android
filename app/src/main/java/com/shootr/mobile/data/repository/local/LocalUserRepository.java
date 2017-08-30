package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.privateMessage.PrivateMessageChannelDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.data.repository.remote.cache.SuggestedPeopleCache;
import com.shootr.mobile.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalUserRepository implements UserRepository {

  public static final int PAGE_SIZE = 100;
  public static final int PAGE = 0;
  private final SessionRepository sessionRepository;
  private final UserDataSource localUserDataSource;
  private final UserEntityMapper userEntityMapper;
  private final StreamEntityMapper streamEntityMapper;
  private final SyncableUserEntityFactory syncableUserEntityFactory;
  private final SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;
  private final SuggestedPeopleCache suggestedPeopleCache;
  private final PrivateMessageChannelDataSource privateMessageChannelDataSource;

  @Inject public LocalUserRepository(SessionRepository sessionRepository,
      @Local UserDataSource userDataSource, UserEntityMapper userEntityMapper,
      StreamEntityMapper streamEntityMapper, SyncableUserEntityFactory syncableUserEntityFactory,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper,
      SuggestedPeopleCache suggestedPeopleCache,
      @Local PrivateMessageChannelDataSource privateMessageChannelDataSource) {
    this.sessionRepository = sessionRepository;
    this.localUserDataSource = userDataSource;
    this.userEntityMapper = userEntityMapper;
    this.streamEntityMapper = streamEntityMapper;
    this.syncableUserEntityFactory = syncableUserEntityFactory;
    this.suggestedPeopleEntityMapper = suggestedPeopleEntityMapper;
    this.suggestedPeopleCache = suggestedPeopleCache;
    this.privateMessageChannelDataSource = privateMessageChannelDataSource;
  }

  @Override public List<User> getPeople() {
    List<UserEntity> userEntities =
        localUserDataSource.getFollowing(sessionRepository.getCurrentUserId(), PAGE, PAGE_SIZE);
    return transformUserEntitiesForPeople(userEntities);
  }

  @Override public User getUserById(String id) {
    return userEntityMapper.transform(localUserDataSource.getUser(id),
        sessionRepository.getCurrentUserId(), isFollower(id), isFollowing(id));
  }

  @Override public User getUserByUsername(String username) {
    return userEntityMapper.transform(localUserDataSource.getUserByUsername(username),
        sessionRepository.getCurrentUserId());
  }

  @Override public boolean isFollower(String userId) {
    return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
  }

  @Override public boolean isFollowing(String userId) {
    return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
  }

  @Override public User putUser(User user) {
    UserEntity currentOrNewEntity = syncableUserEntityFactory.updatedOrNewEntity(user);
    currentOrNewEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    localUserDataSource.putUser(currentOrNewEntity);
    return user;
  }

  @Override public List<SuggestedPeople> getSuggestedPeople(String locale) {
    return suggestedPeopleCache.getSuggestedPeople();
  }

  @Override public List<User> getAllParticipants(String idStream, Long maxJoinDate) {
    throw new IllegalArgumentException("getAllParticipants has no local implementation");
  }

  @Override public List<User> findParticipants(String idStream, String query) {
    throw new IllegalArgumentException("Find Participants has no local implementation");
  }

  @Override public Stream updateWatch(User user) {
    UserEntity entity = userEntityMapper.transform(user);
    entity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    return streamEntityMapper.transform(localUserDataSource.updateWatch(entity));
  }

  @Override public void synchronizeFollow() {
    throw new IllegalArgumentException("Find Participants has no local implementation");
  }

  @Override public List<User> getFollowing(String idUser, Integer page, Integer pageSize) {
    throw new IllegalArgumentException("this method has no local implementation");
  }

  @Override public List<User> getFollowers(String idUser, Integer page, Integer pageSize) {
    throw new IllegalArgumentException("this method has no local implementation");
  }

  @Override public List<User> getLocalPeople(String idUser) {
    return transformUserEntitiesForPeople(localUserDataSource.getRelatedUsers(idUser, 0L));
  }

  @Override public List<User> getLocalPeopleFromIdStream(String idStream) {
    return transformUserEntitiesForPeople(localUserDataSource.getRelatedUsersByIdStream(idStream,
        sessionRepository.getCurrentUserId()));
  }

  @Override public User getUserForAnalythicsById(String id) {
    throw new IllegalArgumentException("this method has no local implementation");
  }

  @Override public void updateUserProfile(User updatedUserEntity) {
    throw new IllegalArgumentException("this method has no local implementation");
  }

  @Override public List<User> findFriends(String searchString, Integer pageOffset, String locale)
      throws IOException {
    return transformUserEntitiesForPeople(
        localUserDataSource.findFriends(searchString, pageOffset, locale));
  }

  private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
    List<User> userList = new ArrayList<>();
    String currentUserId = sessionRepository.getCurrentUserId();
    for (UserEntity localUserEntity : localUserEntities) {
      String idUser = localUserEntity.getIdUser();
      User user = userEntityMapper.transform(localUserEntity, currentUserId, isFollower(idUser),
          isFollowing(idUser));
      userList.add(user);
    }
    return userList;
  }

  private List<SuggestedPeople> suggestedPeopleEntitiesToDomain(
      List<SuggestedPeopleEntity> suggestedPeopleEntities) {
    List<SuggestedPeople> suggestedPeoples = new ArrayList<>(suggestedPeopleEntities.size());
    for (SuggestedPeopleEntity suggestedPeople : suggestedPeopleEntities) {
      suggestedPeoples.add(suggestedPeopleEntityMapper.transform(suggestedPeople));
    }
    return suggestedPeoples;
  }

  @Override public void forceUpdatePeople() {
    throw new IllegalArgumentException("forceUpdatePeople has no local implementation");
  }

  @Override public List<String> getFollowingIds(String userId) {
    return localUserDataSource.getFollowingIds(userId);
  }

  @Override public void mute(String idUser) {
    localUserDataSource.mute(idUser);
    privateMessageChannelDataSource.setPrivateMessageChannelMuted(idUser);
  }

  @Override public void unMute(String idUser) {
    localUserDataSource.unMute(idUser);
    privateMessageChannelDataSource.setPrivateMessageChannelUnMuted(idUser);
  }
}
