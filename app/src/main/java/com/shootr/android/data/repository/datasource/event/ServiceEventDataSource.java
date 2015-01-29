package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceEventDataSource implements EventDataSource {

    private final ShootrService service;

    @Inject public ServiceEventDataSource(ShootrService service) {
        this.service = service;
    }

    @Override public EventEntity getEventById(Long idEvent) {
        try {
            return service.getEventById(idEvent);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public EventEntity putEvent(EventEntity eventEntity) {
        throw new RuntimeException("Method not implemented yet!");
    }
}
