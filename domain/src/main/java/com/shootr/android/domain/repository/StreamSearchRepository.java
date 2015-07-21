package com.shootr.android.domain.repository;

import com.shootr.android.domain.EventSearchResult;
import java.util.List;

public interface StreamSearchRepository {

    List<EventSearchResult> getDefaultStreams(String locale);

    List<EventSearchResult> getStreams(String query, String locale);

    void putDefaultStreams(List<EventSearchResult> eventSearchResults);

    void deleteDefaultStreams();

    List<EventSearchResult> getStreamsListing(String listingIdUser);
}
