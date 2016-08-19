package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.RecentStreamEntity;
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

    public RecentStream transform(RecentStreamEntity recentStreamEntity) {
        if (recentStreamEntity == null) {
            return null;
        }
        RecentStream recentStream = new RecentStream();
        recentStream.setJoinStreamDate(recentStreamEntity.getJoinStreamDate());
        Stream stream = streamEntityMapper.transform(recentStreamEntity.getStream());
        recentStream.setStream(stream);
        return recentStream;
    }

    public RecentStreamEntity transform(RecentStream recentStream) {
        if (recentStream == null) {
            return null;
        }
        RecentStreamEntity recentStreamEntity = new RecentStreamEntity();
        recentStreamEntity.setJoinStreamDate(recentStream.getJoinStreamDate());
        StreamEntity stream = streamEntityMapper.transform(recentStream.getStream());
        recentStreamEntity.setStream(stream);
        return recentStreamEntity;
    }

    public List<StreamSearchResult> transform(List<RecentStreamEntity> recentStreamEntities) {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>(recentStreamEntities.size());
        for (RecentStreamEntity recentStreamEntity : recentStreamEntities) {
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(streamEntityMapper.transform(recentStreamEntity.getStream()));
            streamSearchResults.add(streamSearchResult);
        }
        return streamSearchResults;
    }
}
