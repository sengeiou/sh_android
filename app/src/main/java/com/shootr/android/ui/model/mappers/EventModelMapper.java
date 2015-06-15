package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.EventModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventModelMapper {

    private final SessionRepository sessionRepository;

    @Inject public EventModelMapper(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public EventModel transform(Event event) {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(event.getId());
        eventModel.setTitle(event.getTitle());
        eventModel.setPicture(event.getPicture());
        eventModel.setTag(event.getTag());
        eventModel.setAmIAuthor(event.getAuthorId().equals(sessionRepository.getCurrentUserId()));
        eventModel.setAuthorId(event.getAuthorId());
        eventModel.setAuthorUsername(event.getAuthorUsername());
        return eventModel;
    }

    public List<EventModel> transform(List<Event> events) {
        List<EventModel> models = new ArrayList<>(events.size());
        for (Event event : events) {
            models.add(transform(event));
        }
        return models;
    }
}
