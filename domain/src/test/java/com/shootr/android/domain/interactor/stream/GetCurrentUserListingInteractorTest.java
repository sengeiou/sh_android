package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Listing;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.SpyCallback;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCurrentUserListingInteractorTest {

    public static final String ID_USER = "idUser";
    public static final String ID_STREAM = "idStream";

    @Mock StreamSearchRepository localStreamSearchRepository;
    @Mock StreamSearchRepository remoteStreamSearchRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.ErrorCallback errorCallback;
    @Spy SpyCallback<Listing> spyCallback = new SpyCallback<>();

    private GetCurrentUserListingStreamsInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        interactor = new GetCurrentUserListingStreamsInteractor(interactorHandler,
          postExecutionThread, localStreamSearchRepository, remoteStreamSearchRepository,
          sessionRepository);
    }

    @Test public void shouldReturnListingWithIncludeHoldingAlwaysTrueWhenSearchingInLocal() throws Exception {
        when(localStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.firstResult();
        assertEquals(listing.includesHolding(), true);
    }

    @Test public void shouldReturnListingWithIncludeHoldingAlwaysTrueWhenSearchingInRemote() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.lastResult();
        assertEquals(listing.includesHolding(), true);
    }

    @Test public void shouldReturnListingWithIncludeFavoritedAlwaysFalseWhenSearchingInLocal() throws Exception {
        when(localStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.firstResult();
        assertEquals(listing.includesFavorited(), false);
    }

    @Test public void shouldReturnListingWithIncludeFavoritedAlwaysFalseWhenSearchingInRemote() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.lastResult();
        assertEquals(listing.includesFavorited(), false);
    }

    @Test public void shouldReturnListingWithHoldingStreamsEqualsToStreamsCreatedByUser() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.lastResult();
        assertEquals(listing.getHoldingStreams(), holdingStreams());
    }

    @Test public void shouldReturnListingWithFavoritedStreamsEmpty() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(streamSearchResults());
        when(remoteStreamSearchRepository.getHolderWatchers()).thenReturn(holderWatchers());
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);
        Listing listing = spyCallback.lastResult();
        assertEquals(listing.getFavoritedStreams(), Collections.emptyList());
    }

    @Test public void shouldSearchHoldingStreamsInLocalRepository() throws Exception {
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);

        verify(localStreamSearchRepository).getStreamsListing(anyString());
    }

    @Test public void shouldSearchHoldingStreamsInRemoteRepository() throws Exception {
        interactor.loadCurrentUserListingStreams(spyCallback, errorCallback);

        verify(remoteStreamSearchRepository).getStreamsListing(anyString());
    }

    private List<StreamSearchResult> holdingStreams() {
        List<StreamSearchResult> streams = streamSearchResults();
        streams.get(0).setWatchersNumber(1);
        return streams;
    }

    private Map<String, Integer> holderWatchers() {
        Map<String, Integer> watchers = new HashMap<>();
        watchers.put(ID_STREAM, 1);
        return watchers;
    }

    private List<StreamSearchResult> streamSearchResults() {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>();
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        Stream stream = new Stream();
        stream.setAuthorId(ID_USER);
        stream.setId(ID_STREAM);
        streamSearchResult.setStream(stream);
        streamSearchResults.add(streamSearchResult);
        return streamSearchResults;
    }
}
