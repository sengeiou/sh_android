package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.RecentSearchDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalRecentSearchRepository implements RecentSearchRepository {

  private final RecentSearchDataSource localRecentSearchDataSource;
  private final StreamEntityMapper streamEntityMapper;
  private final UserEntityMapper userEntityMapper;
  private final UserDataSource localUserDataSource;
  private final SessionRepository sessionRepository;

  @Inject public LocalRecentSearchRepository(RecentSearchDataSource localRecentSearchDataSource,
      StreamEntityMapper streamEntityMapper, UserEntityMapper userEntityMapper,
      @Local UserDataSource localUserDataSource, SessionRepository sessionRepository) {
    this.localRecentSearchDataSource = localRecentSearchDataSource;
    this.streamEntityMapper = streamEntityMapper;
    this.userEntityMapper = userEntityMapper;
    this.localUserDataSource = localUserDataSource;
    this.sessionRepository = sessionRepository;
  }

  @Override public void putRecentStream(Stream stream, long currentTime) {
    if (stream != null) {
      localRecentSearchDataSource.putRecentStream(streamEntityMapper.transform(stream),
          currentTime);
    }
  }

  @Override public void putRecentUser(User user, long currentTime) {
    if (user != null) {
      localRecentSearchDataSource.putRecentUser(userEntityMapper.transform(user),
          currentTime);
    }
  }

  @Override public List<Searchable> getDefaultSearch() {

    List<Searchable> searchables = new ArrayList<>();
    List<RecentSearchEntity> searchableEntities = localRecentSearchDataSource.getRecentSearches();
    try {
      for (RecentSearchEntity searchableEntity : searchableEntities) {
        switch (searchableEntity.getSearchableType()) {
          case SearchableType.STREAM:
            searchables.add(streamEntityMapper.transform(searchableEntity.getStream()));
            break;
          case SearchableType.USER:
            UserEntity userEntity = searchableEntity.getUser();
            searchables.add(
                userEntityMapper.transform(userEntity, sessionRepository.getCurrentUserId(),
                    false, isFollowing(userEntity.getIdUser())));
            break;
          default:
            break;
        }
      }
    } catch (NullPointerException e) {
      /* no-op */
    }

    return searchables;
  }

  private boolean isFollowing(String userId) {
    return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
  }
}
