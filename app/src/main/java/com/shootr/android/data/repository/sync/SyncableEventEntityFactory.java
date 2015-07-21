package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableEventEntityFactory extends SyncableEntityFactory<Event, StreamEntity> {

    private EventEntityMapper eventEntityMapper;
    private StreamDataSource localStreamDataSource;

    @Inject public SyncableEventEntityFactory(EventEntityMapper eventEntityMapper,
      @Local StreamDataSource localStreamDataSource) {
        this.eventEntityMapper = eventEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override protected StreamEntity currentEntity(Event event) {
        return localStreamDataSource.getStreamById(event.getId());
    }

    @Override protected StreamEntity updateValues(StreamEntity currentEntity, Event event) {
        StreamEntity streamEntityFromDomain = eventEntityMapper.transform(event);
        streamEntityFromDomain.setModified(new Date());
        streamEntityFromDomain.setRevision(currentEntity.getRevision() + 1);
        streamEntityFromDomain.setBirth(currentEntity.getBirth());
        streamEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        streamEntityFromDomain.setDeleted(currentEntity.getDeleted());
        return streamEntityFromDomain;
    }

    @Override protected StreamEntity createNewEntity(Event event) {
        StreamEntity newEntityFromDomain = eventEntityMapper.transform(event);
        newEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setModified(birth);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
