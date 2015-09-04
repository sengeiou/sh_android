package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class StreamSearchEntityMapper {

    public final StreamEntityMapper streamEntityMapper;

    @Inject public StreamSearchEntityMapper(StreamEntityMapper streamEntityMapper) {
        this.streamEntityMapper = streamEntityMapper;
    }

    public StreamSearchResult transform(StreamSearchEntity streamSearchEntity) {
        Stream stream = streamEntityMapper.transform(streamSearchEntity);

        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(stream);
        streamSearchResult.setWatchersNumber(streamSearchEntity.getTotalWatchers());

        return streamSearchResult;
    }

    public List<StreamSearchResult> transformToDomain(List<StreamSearchEntity> eventSearchEntities) {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>(eventSearchEntities.size());
        for (StreamSearchEntity streamSearchEntity : eventSearchEntities) {
            streamSearchResults.add(transform(streamSearchEntity));
        }
        return streamSearchResults;
    }

    public StreamSearchEntity transform(StreamSearchResult streamSearchResult) {
        Stream stream = streamSearchResult.getStream();

        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        streamEntityMapper.transformToTemplate(stream, streamSearchEntity);

        streamSearchEntity.setTotalWatchers(streamSearchResult.getWatchersNumber());

        streamSearchEntity.setBirth(new Date());
        streamSearchEntity.setModified(new Date());

        return streamSearchEntity;
    }

    public List<StreamSearchEntity> transform(List<StreamSearchResult> streamSearchResults) {
        List<StreamSearchEntity> entities = new ArrayList<>(streamSearchResults.size());
        for (StreamSearchResult streamSearchResult : streamSearchResults) {
            entities.add(transform(streamSearchResult));
        }
        return entities;
    }

}
