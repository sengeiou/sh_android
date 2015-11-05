package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Favorite;
import com.shootr.mobile.domain.Listing;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserListingStreamsInteractorTest {

    public static final String ID_USER = "idUser";
    public static final String ID_STREAM = "idStream";
    @Mock StreamSearchRepository localStreamSearchRepository;
    @Mock StreamSearchRepository remoteStreamSearchRepository;
    @Mock StreamRepository localStreamRepository;
    @Mock StreamRepository remoteStreamRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock SessionRepository sessionRepository;
    @Mock FavoriteRepository localFavoriteRepository;
    @Spy SpyCallback<Listing> spyCallback = new SpyCallback<>();
    private GetUserListingStreamsInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetUserListingStreamsInteractor(interactorHandler,
          postExecutionThread,
          localStreamSearchRepository,
          remoteStreamSearchRepository,
          localStreamRepository,
          remoteStreamRepository,
          localFavoriteRepository);
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    }

    @Test public void shouldReturnListingWithHoldingIfUserHaveHoldingStreams() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.getHoldingStreams(), listingStreams());
    }

    @Test public void shouldReturnListingWithFavoritesIfUserHaveFavoriteStreams() throws Exception {
        when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamRepository.getStreamsByIds(anyList())).thenReturn(favoriteStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.getFavoritedStreams(), favoriteStreamResults());
    }

    @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHaveHoldingStreams() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.includesHolding(), true);
    }

    @Test public void shouldReturnListingWithIncludeFavoritesIfUserHaveFavoriteStreams() throws Exception {
        when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamRepository.getStreamsByIds(anyList())).thenReturn(favoriteStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.includesFavorited(), true);
    }

    @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHoldingStreamsAreEmptyList() throws Exception {
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.includesHolding(), true);
    }

    @Test public void shouldReturnListingWithIncludeFavoritesTrueIfUserFavoriteStreamsAreEmptyList() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.includesFavorited(), true);
    }

    @Test public void shouldLoadUserListingFromLocal() throws Exception {
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

        verify(localStreamSearchRepository).getStreamsListing(anyString());
    }

    @Test public void shouldLoadUserListingFromRemote() throws Exception {
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

        verify(remoteStreamSearchRepository).getStreamsListing(anyString());
    }

    @Test public void shouldLoadFavoritesFromLocal() throws Exception {
        when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

        verify(localStreamRepository).getStreamsByIds(anyList());
    }

    @Test public void shouldLoadFavoritesFromRemote() throws Exception {
        when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

        verify(remoteStreamRepository).getStreamsByIds(anyList());
    }

    @Test public void shouldReturnListingWithoutHoldingIfUserHaveNoHoldingStreams() throws Exception {
        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.getHoldingStreams(), Collections.emptyList());
    }

    @Test public void shouldReturnListingWithoutFavoritesIfUserHaveNoFavoriteStreams() throws Exception {
        when(remoteStreamSearchRepository.getStreamsListing(ID_USER)).thenReturn(listingStreams());
        when(remoteStreamSearchRepository.getHolderFavorites(ID_USER)).thenReturn(holderWatchers());

        interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
        Listing listing = spyCallback.lastResult();

        assertEquals(listing.getFavoritedStreams(), Collections.emptyList());
    }

    private Map<String, Integer> holderWatchers() {
        Map<String, Integer> map = new HashMap<>();
        for (com.shootr.mobile.domain.Stream stream : favoriteStreams()) {
            map.put(stream.getId(), 0);
        }
        return map;
    }

    private List<StreamSearchResult> favoriteStreamResults() {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>();
        for (com.shootr.mobile.domain.Stream stream : favoriteStreams()) {
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(stream);
            streamSearchResults.add(streamSearchResult);
        }
        return streamSearchResults;
    }

    private List<com.shootr.mobile.domain.Stream> favoriteStreams() {
        List<com.shootr.mobile.domain.Stream> streams = new ArrayList<>();
        for (Favorite favorite : favorites()) {
            com.shootr.mobile.domain.Stream stream = new com.shootr.mobile.domain.Stream();
            stream.setId(favorite.getIdStream());
            streams.add(stream);
        }
        return streams;
    }

    private List<StreamSearchResult> listingStreams() {
        List<StreamSearchResult> streamSearchResults = new ArrayList<>();
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        com.shootr.mobile.domain.Stream stream = new com.shootr.mobile.domain.Stream();
        stream.setAuthorId(ID_USER);
        stream.setId(ID_STREAM);
        stream.setRemoved(false);
        streamSearchResult.setStream(stream);
        streamSearchResults.add(streamSearchResult);
        return streamSearchResults;
    }

    private List<Favorite> favorites() {
        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite = new Favorite();
        favorite.setOrder(0);
        favorite.setIdStream(ID_STREAM);
        favorites.add(favorite);
        return favorites;
    }
}
