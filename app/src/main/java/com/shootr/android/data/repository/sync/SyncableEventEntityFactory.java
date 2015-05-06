package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableEventEntityFactory extends SyncableEntityFactory<Event, EventEntity> {

    private EventEntityMapper eventEntityMapper;
    private EventDataSource localEventDataSource;

    @Inject public SyncableEventEntityFactory(EventEntityMapper eventEntityMapper,
      @Local EventDataSource localEventDataSource) {
        this.eventEntityMapper = eventEntityMapper;
        this.localEventDataSource = localEventDataSource;
    }

    @Override protected EventEntity currentEntity(Event event) {
        return localEventDataSource.getEventById(event.getId());
    }

    @Override protected EventEntity updateValues(EventEntity currentEntity, Event event) {
        EventEntity eventEntityFromDomain = eventEntityMapper.transform(event);
        eventEntityFromDomain.setCsysModified(new Date());
        eventEntityFromDomain.setCsysRevision(currentEntity.getCsysRevision() + 1);
        eventEntityFromDomain.setCsysBirth(currentEntity.getCsysBirth());
        eventEntityFromDomain.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        eventEntityFromDomain.setCsysDeleted(currentEntity.getCsysDeleted());
        return eventEntityFromDomain;
    }

    @Override protected EventEntity createNewEntity(Event event) {
        EventEntity newEntityFromDomain = eventEntityMapper.transform(event);
        newEntityFromDomain.setCsysSynchronized(Synchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setCsysBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setCsysModified(birth);
        newEntityFromDomain.setCsysRevision(0);
        return newEntityFromDomain;
    }
}
