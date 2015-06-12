package com.shootr.android.domain.repository;

import com.shootr.android.domain.EventSearchResult;
import java.util.List;

public interface EventSearchRepository {

    List<EventSearchResult> getDefaultEvents(String locale);

    List<EventSearchResult> getEvents(String query, String locale);

    void putDefaultEvents(List<EventSearchResult> eventSearchResults);

    void deleteDefaultEvents();

    List<EventSearchResult> getEventsListing(String idUser, String listingIdUser, String locale);
}
