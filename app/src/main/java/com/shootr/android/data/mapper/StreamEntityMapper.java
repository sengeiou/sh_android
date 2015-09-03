package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.Stream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamEntityMapper {

    private final UserEntityMapper userEntityMapper;

    @Inject public StreamEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    public Stream transform(StreamEntity streamEntity) {
        if (streamEntity == null) {
            return null;
        }
        Stream stream = new Stream();
        stream.setId(streamEntity.getIdStream());
        stream.setAuthorId(streamEntity.getIdUser());
        stream.setTitle(streamEntity.getTitle());
        stream.setPicture(streamEntity.getPhoto());
        stream.setTag(streamEntity.getTag());
        stream.setAuthorUsername(streamEntity.getUserName());
        stream.setLocale(streamEntity.getLocale());
        stream.setDescription(streamEntity.getDescription());
        stream.setMediaCount(streamEntity.getMediaCountByRelatedUsers());
        stream.setRemoved(streamEntity.getRemoved() == 1);
        if (streamEntity.getWatchers() != null) {
            stream.setWatchers(userEntityMapper.transformEntities(streamEntity.getWatchers()));
        }
        return stream;
    }

    public List<Stream> transform(List<StreamEntity> eventEntities) {
        List<Stream> streams = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            streams.add(transform(streamEntity));
        }
        return streams;
    }

    public StreamEntity transform(Stream stream) {
        StreamEntity streamEntity = new StreamEntity();
        transformToTemplate(stream, streamEntity);
        return streamEntity;
    }

    protected void transformToTemplate(Stream stream, StreamEntity entityTemplate) {
        entityTemplate.setIdStream(stream.getId());
        entityTemplate.setIdUser(stream.getAuthorId());
        entityTemplate.setTitle(stream.getTitle());
        entityTemplate.setPhoto(stream.getPicture());
        entityTemplate.setTag(stream.getTag());
        entityTemplate.setUserName(stream.getAuthorUsername());
        entityTemplate.setLocale(stream.getLocale());
        entityTemplate.setDescription(stream.getDescription());
        entityTemplate.setMediaCountByRelatedUsers(stream.getMediaCount());

        entityTemplate.setRemoved(stream.isRemoved() ? 1 : 0);
        entityTemplate.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
    }
}
