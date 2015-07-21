package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableEventEntityFactory extends SyncableEntityFactory<Event, EventEntity> {

    private EventEntityMapper eventEntityMapper;
    private StreamDataSource localStreamDataSource;

    @Inject public SyncableEventEntityFactory(EventEntityMapper eventEntityMapper,
      @Local StreamDataSource localStreamDataSource) {
        this.eventEntityMapper = eventEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override protected EventEntity currentEntity(Event event) {
        return localStreamDataSource.getStreamById(event.getId());
    }

    @Override protected EventEntity updateValues(EventEntity currentEntity, Event event) {
        EventEntity eventEntityFromDomain = eventEntityMapper.transform(event);
        eventEntityFromDomain.setModified(new Date());
        eventEntityFromDomain.setRevision(currentEntity.getRevision() + 1);
        eventEntityFromDomain.setBirth(currentEntity.getBirth());
        eventEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        eventEntityFromDomain.setDeleted(currentEntity.getDeleted());
        return eventEntityFromDomain;
    }

    @Override protected EventEntity createNewEntity(Event event) {
        EventEntity newEntityFromDomain = eventEntityMapper.transform(event);
        newEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setModified(birth);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
