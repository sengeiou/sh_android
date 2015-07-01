package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventSearchDataSource implements EventSearchDataSource {

    @Inject public ServiceEventSearchDataSource() {
    }

    @Override public List<EventSearchEntity> getDefaultEvents(String locale) {
        throw new IllegalStateException("Method not implemented in remote datasource");
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
}
