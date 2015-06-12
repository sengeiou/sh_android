package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventDataSource implements EventDataSource {

    public static final int MAX_NUMBER_OF_LISTING_EVENTS = 100;
    private final ShootrService service;
    private final EventApiService eventService;

    @Inject public ServiceEventDataSource(ShootrService service, EventApiService eventService) {
        this.service = service;
        this.eventService = eventService;
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

    @Override public Integer getListingCount(String idUser) {
        try {
            return service.getListingCount(idUser);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> getEventsListing(String me, String idUser, String locale) {
        try {
            return eventService.getEventListing(me, idUser, locale, MAX_NUMBER_OF_LISTING_EVENTS);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
