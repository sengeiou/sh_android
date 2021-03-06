package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.SearchableEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.searchItem.ExternalSearchItemDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.searchItem.ExternalSearchItemRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncSearchItemRepository implements ExternalSearchItemRepository {

  private final ExternalSearchItemDataSource externalSearchItemDataSource;
  private final UserEntityMapper userEntityMapper;
  private final StreamEntityMapper streamEntityMapper;
  private final UserDataSource localUserDataSource;
  private final SessionRepository sessionRepository;

  @Inject public SyncSearchItemRepository(ExternalSearchItemDataSource externalSearchItemDataSource,
      UserEntityMapper userEntityMapper, StreamEntityMapper streamEntityMapper,
      @Local UserDataSource localUserDataSource, SessionRepository sessionRepository) {
    this.externalSearchItemDataSource = externalSearchItemDataSource;
    this.userEntityMapper = userEntityMapper;
    this.streamEntityMapper = streamEntityMapper;
    this.localUserDataSource = localUserDataSource;
    this.sessionRepository = sessionRepository;
  }

  @Override public List<Searchable> getSearch(String query, String[] type) {

    List<Searchable> searchables = new ArrayList<>();
    List<SearchableEntity> searchableEntities = externalSearchItemDataSource.getSearch(query, type);
    try {

      for (SearchableEntity searchableEntity : searchableEntities) {
        switch (searchableEntity.getSearcheableType()) {
          case SearchableType.STREAM:
            searchables.add(streamEntityMapper.transform((StreamEntity) searchableEntity,
                ((StreamEntity) searchableEntity).isFollowing()));
            break;
          case SearchableType.USER:
            UserEntity userEntity = (UserEntity) searchableEntity;
            searchables.add(
                userEntityMapper.transform(userEntity, sessionRepository.getCurrentUserId()));
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
}
