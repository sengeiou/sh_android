package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import java.util.List;

public interface EventSearchDataSource {

    List<EventSearchEntity> getDefaultEvents(String locale);

    void putDefaultEvents(List<EventSearchEntity> transform);

    void deleteDefaultEvents();

    EventSearchEntity getEventResult(String idEvent);
}
