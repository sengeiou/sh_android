package com.shootr.mobile.data.repository.remote;

import android.util.Log;
import com.shootr.mobile.data.entity.SearchableEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.data.repository.datasource.searchItem.ExternalSearchItemDataSource;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.repository.searchItem.ExternalSearchItemRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncSearchItemRepository implements ExternalSearchItemRepository {

  private final ExternalSearchItemDataSource externalSearchItemDataSource;
  private final UserEntityMapper userEntityMapper;
  private final StreamEntityMapper streamEntityMapper;

  @Inject public SyncSearchItemRepository(ExternalSearchItemDataSource externalSearchItemDataSource,
      UserEntityMapper userEntityMapper, StreamEntityMapper streamEntityMapper) {
    this.externalSearchItemDataSource = externalSearchItemDataSource;
    this.userEntityMapper = userEntityMapper;
    this.streamEntityMapper = streamEntityMapper;
  }

  @Override public List<Searchable> getSearch(String query, String[] type) {

    List<Searchable> searchables = new ArrayList<>();
    List<SearchableEntity> searchableEntities = externalSearchItemDataSource.getSearch(query, type);
    int i = 0;
    try {

      for (SearchableEntity searchableEntity : searchableEntities) {
        switch (searchableEntity.getSearcheableType()) {
          case SearchableType.STREAM:
            searchables.add(streamEntityMapper.transform((StreamEntity) searchableEntity));
            break;
          case SearchableType.USER:
            searchables.add(userEntityMapper.transform((UserEntity) searchableEntity));
            break;
          default:
            break;
        }
        i++;
      }
    } catch (NullPointerException e) {
      Log.d("peto", "peto en linea " + i);
    }

    return searchables;
  }
}
