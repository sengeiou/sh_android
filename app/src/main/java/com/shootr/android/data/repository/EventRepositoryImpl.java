package com.shootr.android.data.repository;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

@Deprecated
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

    @Override public Event getEventById(Long idEvent) {
        EventEntity eventEntity = eventManager.getEventById(idEvent);
        return eventEntityMapper.transform(eventEntity);
    }

    @Override public List<Event> getEventsByIds(List<Long> eventIds) {
        throw new RuntimeException("Method not implemented here.");
    }
}
