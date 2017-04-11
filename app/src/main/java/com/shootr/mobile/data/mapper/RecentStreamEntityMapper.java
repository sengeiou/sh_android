package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.stream.RecentStream;
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

    public RecentStream transform(RecentSearchEntity recentSearchEntity) {
        if (recentSearchEntity == null) {
            return null;
        }
        RecentStream recentStream = new RecentStream();
        recentStream.setJoinStreamDate(recentSearchEntity.getVisitDate());
        Stream stream = streamEntityMapper.transform(recentSearchEntity.getStream());
        recentStream.setStream(stream);
        return recentStream;
    }

    public RecentSearchEntity transform(RecentStream recentStream) {
        if (recentStream == null) {
            return null;
        }
        RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
        recentSearchEntity.setVisitDate(recentStream.getJoinStreamDate());
        StreamEntity stream = streamEntityMapper.transform(recentStream.getStream());
        recentSearchEntity.setStream(stream);
        return recentSearchEntity;
    }

    public List<StreamSearchResult> transform(List<RecentSearchEntity> recentStreamEntities) {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>(recentStreamEntities.size());
        for (RecentSearchEntity recentSearchEntity : recentStreamEntities) {
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(streamEntityMapper.transform(recentSearchEntity.getStream()));
            streamSearchResults.add(streamSearchResult);
        }
        return streamSearchResults;
    }
}
