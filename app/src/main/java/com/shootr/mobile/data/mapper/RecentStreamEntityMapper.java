package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.stream.RecentSearch;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RecentStreamEntityMapper {

    private final StreamEntityMapper streamEntityMapper;

    @Inject public RecentStreamEntityMapper(StreamEntityMapper streamEntityMapper) {
        this.streamEntityMapper = streamEntityMapper;
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

    public RecentSearchEntity transform(RecentSearch recentSearch) {
        if (recentSearch == null) {
            return null;
        }
        RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
        recentSearchEntity.setVisitDate(recentSearch.getJoinStreamDate());
        StreamEntity stream = streamEntityMapper.transform(recentSearch.getStream());
        recentSearchEntity.setStream(stream);
        return recentSearchEntity;
    }

    public List<Searchable> transform(List<RecentSearchEntity> recentStreamEntities) {
        List<Searchable> searchableSearchResults = new ArrayList<>(recentStreamEntities.size());
        /*for (RecentSearchEntity recentSearchEntity : recentStreamEntities) {
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(streamEntityMapper.transform(recentSearchEntity.getStream()));
            searchableSearchResults.add(streamSearchResult);
        }*/
        return searchableSearchResults;
    }
}
