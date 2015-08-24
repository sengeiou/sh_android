package com.shootr.android.domain.repository;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.Watchers;
import java.util.List;

public interface StreamSearchRepository {

    List<StreamSearchResult> getDefaultStreams(String locale);

    List<StreamSearchResult> getStreams(String query, String locale);

    void putDefaultStreams(List<StreamSearchResult> streamSearchResults);

    void deleteDefaultStreams();

    List<StreamSearchResult> getStreamsListing(String listingIdUser);

    Integer getWatchersForStream(String idStream);

    List<Watchers> getWatchers();
}
