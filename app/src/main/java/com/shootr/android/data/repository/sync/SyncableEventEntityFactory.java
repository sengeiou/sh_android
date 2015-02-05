package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.WatchEntity;
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

    @Override protected EventEntity updateValues(EventEntity currentWatchEntity, Event event) {
        EventEntity eventEntityFromWatch = eventEntityMapper.transform(event);
        eventEntityFromWatch.setCsysModified(new Date());
        eventEntityFromWatch.setCsysRevision(currentWatchEntity.getCsysRevision() + 1);
        eventEntityFromWatch.setCsysBirth(currentWatchEntity.getCsysBirth());
        eventEntityFromWatch.setCsysSynchronized(Synchronized.SYNC_UPDATED);
        eventEntityFromWatch.setCsysDeleted(currentWatchEntity.getCsysDeleted());
        return eventEntityFromWatch;
    }

    @Override protected EventEntity createNewEntity(Event event) {
        EventEntity newEntityFromWatch = eventEntityMapper.transform(event);
        newEntityFromWatch.setCsysSynchronized(Synchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromWatch.setCsysBirth(birth); //TODO dates from timeutils
        newEntityFromWatch.setCsysModified(birth);
        newEntityFromWatch.setCsysRevision(0);
        return newEntityFromWatch;
    }
}
