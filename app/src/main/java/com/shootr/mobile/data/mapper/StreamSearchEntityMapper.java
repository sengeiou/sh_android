package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
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
