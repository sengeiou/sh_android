package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.domain.EntityMetadata;

import javax.inject.Inject;

public class MetadataMapper {

    @Inject public MetadataMapper() {
    }

    public EntityMetadata metadataFromEntity(Synchronized entity) {
        EntityMetadata metadata = new EntityMetadata();
        metadata.setBirth(entity.getBirth());
        metadata.setModified(entity.getModified());
        metadata.setDeleted(entity.getDeleted());
        metadata.setRevision(entity.getRevision());
        metadata.setSynchronizedStatus(entity.getSynchronizedStatus());
        return metadata;
    }

    public void fillEntityWithMetadata(Synchronized entity, EntityMetadata metadata) {
        if (metadata != null) {
            entity.setBirth(metadata.getBirth());
            entity.setModified(metadata.getModified());
            entity.setDeleted(metadata.getDeleted());
            entity.setRevision(metadata.getRevision());
            entity.setSynchronizedStatus(metadata.getSynchronizedStatus());
        }
    }
}