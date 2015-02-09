package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import java.util.List;

public interface EventSearchDataSource {

    List<EventSearchEntity> getDefaultEvents();

    List<EventSearchEntity> getEvents(String query);

}
