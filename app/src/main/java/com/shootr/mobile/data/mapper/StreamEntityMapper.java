package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.Stream;
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
        stream.setShortTitle(streamEntity.getShortTitle());
        stream.setAuthorUsername(streamEntity.getUserName());
        stream.setCountry(streamEntity.getCountry());
        stream.setDescription(streamEntity.getDescription());
        stream.setMediaCount(streamEntity.getMediaCountByRelatedUsers());
        stream.setRemoved(streamEntity.getRemoved() == 1);
        if (streamEntity.getWatchers() != null) {
            stream.setWatchers(userEntityMapper.transformEntities(streamEntity.getWatchers()));
        }
        stream.setTotalFavorites(streamEntity.getTotalFavorites() != null ? streamEntity.getTotalFavorites().intValue() : 0);
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
        entityTemplate.setShortTitle(stream.getShortTitle());
        entityTemplate.setUserName(stream.getAuthorUsername());
        entityTemplate.setCountry(stream.getCountry());
        entityTemplate.setDescription(stream.getDescription());
        entityTemplate.setMediaCountByRelatedUsers(stream.getMediaCount());

        entityTemplate.setRemoved(stream.isRemoved() ? 1 : 0);
        entityTemplate.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        entityTemplate.setTotalFavorites(Long.valueOf(stream.getTotalFavorites()));
    }
}