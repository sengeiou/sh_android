package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamEntity;
import java.util.List;

public interface EventListDataSource {

    List<StreamEntity> getEventList(String locale);

    List<StreamEntity> getEvents(String query, String locale);
}
