package com.shootr.android.domain.repository;

import com.shootr.android.domain.EventSearchResult;
import java.util.List;

public interface EventSearchRepository {

    List<EventSearchResult> getDefaultEvents();

    List<EventSearchResult> getEvents(String query);

    void putDefaultEvents(List<EventSearchResult> eventSearchResults);

    void deleteDefaultEvents();
}
