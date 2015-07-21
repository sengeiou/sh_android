package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamSearchEntity;
import java.util.List;
import javax.inject.Inject;

public class ServiceEventSearchDataSource implements EventSearchDataSource {

    @Inject public ServiceEventSearchDataSource() {
    }

    @Override public List<StreamSearchEntity> getDefaultEvents(String locale) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void putDefaultEvents(List<StreamSearchEntity> transform) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }

    @Override public StreamSearchEntity getEventResult(String idEvent) {
        throw new IllegalStateException("Method not implemented in remote datasource");
    }
}
