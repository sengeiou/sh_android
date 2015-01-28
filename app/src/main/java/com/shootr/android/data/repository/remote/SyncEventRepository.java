package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncEventRepository implements EventRepository, SyncableRepository {

    //TODO Mock!!! Éste no debe ser local
    private final WatchManager watchManager;
    private final SessionRepository sessionRepository;
    private final EventManager eventManager;
    private final EventEntityMapper eventEntityMapper;

    @Inject public SyncEventRepository(WatchManager watchManager, SessionRepository sessionRepository,
      EventManager eventManager, EventEntityMapper eventEntityMapper) {
        this.watchManager = watchManager;
        this.sessionRepository = sessionRepository;
        this.eventManager = eventManager;
        this.eventEntityMapper = eventEntityMapper;
    }

    //TODO huele a que este método corresponde a WatchRepository devolviendo un Watch
    @Override public Event getVisibleEvent() {
        WatchEntity watchVisibleByUser = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());
        if (watchVisibleByUser == null) {
            return null;
        }

        Long idEvent = watchVisibleByUser.getIdEvent();
        EventEntity eventEntity = eventManager.getEventById(idEvent);
        if (eventEntity == null) {
            Timber.e("There is a visible event watch, and the event is not in database");
            return null;
        }
        return eventEntityMapper.transform(eventEntity);
    }

    @Override public Event getEventById(Long idEvent) {
        EventEntity eventEntity = eventManager.getEventById(idEvent);
        return eventEntityMapper.transform(eventEntity);
    }
    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
