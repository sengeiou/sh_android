package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.StreamEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableEventEntityFactory extends SyncableEntityFactory<Stream, StreamEntity> {

    private StreamEntityMapper streamEntityMapper;
    private StreamDataSource localStreamDataSource;

    @Inject public SyncableEventEntityFactory(StreamEntityMapper streamEntityMapper,
      @Local StreamDataSource localStreamDataSource) {
        this.streamEntityMapper = streamEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override protected StreamEntity currentEntity(Stream stream) {
        return localStreamDataSource.getStreamById(stream.getId());
    }

    @Override protected StreamEntity updateValues(StreamEntity currentEntity, Stream stream) {
        StreamEntity streamEntityFromDomain = streamEntityMapper.transform(stream);
        streamEntityFromDomain.setModified(new Date());
        streamEntityFromDomain.setRevision(currentEntity.getRevision() + 1);
        streamEntityFromDomain.setBirth(currentEntity.getBirth());
        streamEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        streamEntityFromDomain.setDeleted(currentEntity.getDeleted());
        return streamEntityFromDomain;
    }

    @Override protected StreamEntity createNewEntity(Stream stream) {
        StreamEntity newEntityFromDomain = streamEntityMapper.transform(stream);
        newEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setModified(birth);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
