package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventDataSource implements EventDataSource {

    private final ShootrService service;

    @Inject public ServiceEventDataSource(ShootrService service) {
        this.service = service;
    }

    @Override public EventEntity getEventById(String idEvent) {
        try {
            return service.getEventById(idEvent);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> getEventsByIds(List<Long> eventIds) {
        try {
            return service.getEventsByIds(eventIds);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public EventEntity putEvent(EventEntity eventEntity) {
        try {
            return service.saveEvent(eventEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> putEvents(List<EventEntity> events) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Integer getEventsListingNumber(String idUser) {
        try {
            return service.getEventsListingNumber(idUser);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
