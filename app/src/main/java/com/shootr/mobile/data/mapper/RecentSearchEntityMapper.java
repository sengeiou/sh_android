package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.stream.RecentSearch;
import com.shootr.mobile.domain.model.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RecentSearchEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final UserEntityMapper userEntityMapper;

  @Inject public RecentSearchEntityMapper(StreamEntityMapper streamEntityMapper,
      UserEntityMapper userEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.userEntityMapper = userEntityMapper;
  }

  public RecentSearch transform(RecentSearchEntity recentSearchEntity) {
    if (recentSearchEntity == null) {
      return null;
    }
    RecentSearch recentSearch = new RecentSearch();
    recentSearch.setJoinStreamDate(recentSearchEntity.getVisitDate());
    Stream stream = streamEntityMapper.transform(recentSearchEntity.getStream());
    recentSearch.setStream(stream);
    return recentSearch;
  }

  public List<Searchable> transform(List<RecentSearchEntity> recentStreamEntities) {
    List<Searchable> searchableSearchResults = new ArrayList<>(recentStreamEntities.size());
    for (RecentSearchEntity recentSearchEntity : recentStreamEntities) {
      if (recentSearchEntity.getSearchableType().equals(SearchableType.STREAM)) {
        searchableSearchResults.add(streamEntityMapper.transform(recentSearchEntity.getStream()));
      } else {
        searchableSearchResults.add(userEntityMapper.transform(recentSearchEntity.getUser()));
      }
    }
    return searchableSearchResults;
  }
}
