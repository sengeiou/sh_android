package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.service.EventSearchApiService;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventSearchDataSource implements EventSearchDataSource {

    private final EventSearchApiService eventSearchApiService;

    @Inject public ServiceEventSearchDataSource(EventSearchApiService eventSearchApiService) {
        this.eventSearchApiService = eventSearchApiService;
    }

    @Override public List<EventSearchEntity> getDefaultEvents(String locale) {
        return loadEvents(null, locale);
    }

    @Override public List<EventSearchEntity> getEvents(String query, String locale) {
        return loadEvents(query, locale);
    }

    @Override public void putDefaultEvents(List<EventSearchEntity> transform) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public EventSearchEntity getEventResult(String idEvent) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    private List<EventSearchEntity> loadEvents(String query, String locale) {
        try {
            return eventSearchApiService.getEvents(query, locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
