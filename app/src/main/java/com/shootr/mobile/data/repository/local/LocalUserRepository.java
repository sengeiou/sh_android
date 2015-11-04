package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.user.SuggestedPeopleDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalUserRepository implements UserRepository {

    private final SessionRepository sessionRepository;
    private final UserDataSource localUserDataSource;
    private final SuggestedPeopleDataSource localSuggestedPeopleDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;
    private final SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;

    @Inject public LocalUserRepository(SessionRepository sessionRepository,
      @Local UserDataSource userDataSource,
      @Local SuggestedPeopleDataSource localSuggestedPeopleDataSource,
      UserEntityMapper userEntityMapper,
      SyncableUserEntityFactory syncableUserEntityFactory,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper) {
        this.sessionRepository = sessionRepository;
        this.localUserDataSource = userDataSource;
        this.localSuggestedPeopleDataSource = localSuggestedPeopleDataSource;
        this.userEntityMapper = userEntityMapper;
        this.syncableUserEntityFactory = syncableUserEntityFactory;
        this.suggestedPeopleEntityMapper = suggestedPeopleEntityMapper;
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

    @Override public List<SuggestedPeople> getSuggestedPeople() {
        List<SuggestedPeopleEntity> suggestedPeople =
          localSuggestedPeopleDataSource.getSuggestedPeople();
        return suggestedPeopleEntitiesToDomain(suggestedPeople);
    }

    @Override public List<User> getAllParticipants(String idStream, Long maxJoinDate) {
        throw new IllegalArgumentException("getAllParticipants has no local implementation");
    }

    @Override public List<User> findParticipants(String idStream, String query) {
        throw new IllegalArgumentException("Find Participants has no local implementation");
    }

    @Override
    public void updateWatch(User user) {
        UserEntity entity = userEntityMapper.transform(user);
        localUserDataSource.updateWatch(entity);
    }

    private List<User> transformUserEntitiesForPeople(List<UserEntity> localUserEntities) {
        List<User> userList = new ArrayList<>();
        String currentUserId = sessionRepository.getCurrentUserId();
        for (UserEntity localUserEntity : localUserEntities) {
            String idUser = localUserEntity.getIdUser();
            User user = userEntityMapper.transform(localUserEntity, currentUserId, isFollower(idUser), isFollowing(idUser));
            userList.add(user);
        }
        return userList;
    }

    private List<SuggestedPeople> suggestedPeopleEntitiesToDomain(List<SuggestedPeopleEntity> suggestedPeopleEntities) {
        List<SuggestedPeople> suggestedPeoples = new ArrayList<>(suggestedPeopleEntities.size());
        for (SuggestedPeopleEntity suggestedPeople : suggestedPeopleEntities) {
            suggestedPeoples.add(suggestedPeopleEntityMapper.transform(suggestedPeople));
        }
        return suggestedPeoples;
    }
}
