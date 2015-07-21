package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventListDataSource implements EventListDataSource {

    private final EventApiService eventService;

    @Inject public ServiceEventListDataSource(EventApiService eventService) {
        this.eventService = eventService;
    }

    @Override
    public List<StreamEntity> getEventList(String locale) {
        try {
            return eventService.getEventList(locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> getEvents(String query, String locale) {
        try {
            return eventService.getEvents(query, locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
