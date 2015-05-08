package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.util.EventTimeFormatter;

import javax.inject.Inject;

public class EventModelMapper {

    private final EventTimeFormatter timeFormatter;
    private final SessionRepository sessionRepository;

    @Inject public EventModelMapper(EventTimeFormatter timeFormatter, SessionRepository sessionRepository) {
        this.timeFormatter = timeFormatter;
        this.sessionRepository = sessionRepository;
    }

    public EventModel transform(Event event) {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(event.getId());
        eventModel.setTitle(event.getTitle());
        eventModel.setPicture(event.getPicture());
        eventModel.setTag(event.getTag());
        long startDateMilliseconds = event.getStartDate().getTime();
        eventModel.setDatetime(timeFormatter.eventResultDateText(startDateMilliseconds));
        eventModel.setStartDate(startDateMilliseconds);
        eventModel.setAmIAuthor(event.getAuthorId().equals(sessionRepository.getCurrentUserId()));
        eventModel.setTimezone(event.getTimezone());
        eventModel.setAuthorId(event.getAuthorId());
        eventModel.setAuthorUsername(event.getAuthorUsername());
        return eventModel;
    }

}
