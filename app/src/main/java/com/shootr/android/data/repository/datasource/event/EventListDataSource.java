package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import java.util.List;

public interface EventListDataSource {

    List<EventEntity> getEventList(String currentUserId, String locale);

    List<EventEntity> getEvents(String query, String locale);
}
