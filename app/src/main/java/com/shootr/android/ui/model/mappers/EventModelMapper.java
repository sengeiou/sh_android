package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Stream;
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

    public EventModel transform(Stream stream) {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(stream.getId());
        eventModel.setTitle(stream.getTitle());
        eventModel.setPicture(stream.getPicture());
        eventModel.setTag(stream.getTag());
        eventModel.setAmIAuthor(stream.getAuthorId().equals(sessionRepository.getCurrentUserId()));
        eventModel.setAuthorId(stream.getAuthorId());
        eventModel.setAuthorUsername(stream.getAuthorUsername());
        return eventModel;
    }

    public List<EventModel> transform(List<Stream> streams) {
        List<EventModel> models = new ArrayList<>(streams.size());
        for (Stream stream : streams) {
            models.add(transform(stream));
        }
        return models;
    }
}
