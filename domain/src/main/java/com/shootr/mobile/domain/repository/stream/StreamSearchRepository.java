package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import java.util.List;

public interface StreamSearchRepository {

    List<StreamSearchResult> getStreamsListing(String listingIdUser, String[] types);

}
