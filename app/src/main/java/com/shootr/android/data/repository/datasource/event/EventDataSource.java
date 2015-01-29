package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;

public interface EventDataSource {

    EventEntity getEventById(Long idEvent);

    EventEntity putEvent(EventEntity eventEntity);
}
