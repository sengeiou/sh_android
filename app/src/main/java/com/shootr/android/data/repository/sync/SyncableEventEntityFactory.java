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
        eventEntityFromDomain.setModified(new Date());
        eventEntityFromDomain.setRevision(currentEntity.getRevision() + 1);
        eventEntityFromDomain.setBirth(currentEntity.getBirth());
        eventEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
        eventEntityFromDomain.setDeleted(currentEntity.getDeleted());
        return eventEntityFromDomain;
    }

    @Override protected EventEntity createNewEntity(Event event) {
        EventEntity newEntityFromDomain = eventEntityMapper.transform(event);
        newEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_NEW);
        Date birth = new Date();
        newEntityFromDomain.setBirth(birth); //TODO dates from timeutils
        newEntityFromDomain.setModified(birth);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
