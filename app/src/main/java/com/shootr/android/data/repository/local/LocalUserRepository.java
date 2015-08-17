package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.SuggestedPeopleEntityMapper;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.datasource.user.CachedSuggestedPeopleDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.domain.SuggestedPeople;
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
    private final CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource;
    private final UserEntityMapper userEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;
    private final SuggestedPeopleEntityMapper suggestedPeopleEntityMapper;

    @Inject public LocalUserRepository(SessionRepository sessionRepository,
      @Local UserDataSource userDataSource,
      CachedSuggestedPeopleDataSource cachedSuggestedPeopleDataSource,
      UserEntityMapper userEntityMapper,
      SyncableUserEntityFactory syncableUserEntityFactory,
      SuggestedPeopleEntityMapper suggestedPeopleEntityMapper) {
        this.sessionRepository = sessionRepository;
        localUserDataSource = userDataSource;
        this.cachedSuggestedPeopleDataSource = cachedSuggestedPeopleDataSource;
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
          cachedSuggestedPeopleDataSource.getSuggestedPeople(sessionRepository.getCurrentUserId());
        return suggestedPeopleEntitiesToDomain(suggestedPeople);
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

    private List<SuggestedPeople> suggestedPeopleEntitiesToDomain(List<SuggestedPeopleEntity> suggestedPeopleEntities) {
        List<SuggestedPeople> suggestedPeoples = new ArrayList<>(suggestedPeopleEntities.size());
        for (SuggestedPeopleEntity suggestedPeople : suggestedPeopleEntities) {
            suggestedPeoples.add(suggestedPeopleEntityMapper.transform(suggestedPeople));
        }
        return suggestedPeoples;
    }
}
