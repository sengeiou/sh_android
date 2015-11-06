package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.StreamSearchResult;
import java.util.List;
import java.util.Map;

public interface StreamSearchRepository {

    List<StreamSearchResult> getDefaultStreams(String locale);

    List<StreamSearchResult> getStreams(String query, String locale);

    void putDefaultStreams(List<StreamSearchResult> streamSearchResults);

    void deleteDefaultStreams();

    List<StreamSearchResult> getStreamsListing(String listingIdUser);

    Map<String, Integer> getHolderFavorites(String idUser);
}
