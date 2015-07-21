package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamSearchEntity;
import java.util.List;

public interface EventSearchDataSource {

    List<StreamSearchEntity> getDefaultEvents(String locale);

    void putDefaultEvents(List<StreamSearchEntity> transform);

    void deleteDefaultEvents();

    StreamSearchEntity getEventResult(String idEvent);
}
