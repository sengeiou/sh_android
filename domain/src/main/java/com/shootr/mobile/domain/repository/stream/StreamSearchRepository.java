package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import java.util.List;

public interface StreamSearchRepository {

    List<StreamSearchResult> getDefaultStreams(String locale, String[] types);

    List<StreamSearchResult> getStreams(String query, String locale, String[] types);

    List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types);

}