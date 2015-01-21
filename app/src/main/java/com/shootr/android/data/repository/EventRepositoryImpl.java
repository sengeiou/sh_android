package com.shootr.android.data.repository;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import javax.inject.Inject;

public class EventRepositoryImpl implements EventRepository {

    private final WatchManager watchManager;
    private final SessionRepository sessionRepository;
    private final EventManager eventManager;
    private final EventEntityMapper eventEntityMapper;

    @Inject public EventRepositoryImpl(WatchManager watchManager, SessionRepository sessionRepository,
      EventManager eventManager, EventEntityMapper eventEntityMapper) {
        this.watchManager = watchManager;
        this.sessionRepository = sessionRepository;
        this.eventManager = eventManager;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getVisibleEvent() {
        WatchEntity watchVisibleByUser = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());
        if (watchVisibleByUser == null) {
            return null;
        }

        Long idEvent = watchVisibleByUser.getIdEvent();
        EventEntity eventEntity = eventManager.getEventById(idEvent);
        return eventEntityMapper.transform(eventEntity);
    }

    @Override public Event getEventById(Long idEvent) {
        EventEntity eventEntity = eventManager.getEventById(idEvent);
        return eventEntityMapper.transform(eventEntity);
    }
}
