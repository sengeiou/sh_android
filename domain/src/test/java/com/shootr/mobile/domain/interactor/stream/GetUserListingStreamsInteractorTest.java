package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.Listing;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserListingStreamsInteractorTest {

  public static final String ID_USER = "idUser";
  public static final String ID_STREAM = "idStream";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
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
    interactor = new GetUserListingStreamsInteractor(interactorHandler, postExecutionThread,
        localStreamSearchRepository, remoteStreamSearchRepository, localStreamRepository,
        remoteStreamRepository, remoteFavoriteRepository);
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
  }

  @Test public void shouldReturnListingWithHoldingIfUserHaveHoldingStreams() throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getHoldingStreams(), listingStreams());
  }

  @Test public void shouldReturnListingWithFavoritesIfUserHaveFavoriteStreams() throws Exception {
    when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteStreamRepository.getStreamsByIds(anyList(), anyArray())).thenReturn(
        favoriteStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getFavoritedStreams(), favoriteStreamResults());
  }

  @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHaveHoldingStreams()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesHolding(), true);
  }

  @Test public void shouldReturnListingWithIncludeFavoritesIfUserHaveFavoriteStreams()
      throws Exception {
    when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteStreamRepository.getStreamsByIds(anyList(), anyArray())).thenReturn(
        favoriteStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesFavorited(), true);
  }

  @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHoldingStreamsAreEmptyList()
      throws Exception {
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesHolding(), true);
  }

  @Test public void shouldReturnListingWithIncludeFavoritesTrueIfUserFavoriteStreamsAreEmptyList()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesFavorited(), true);
  }

  @Test public void shouldLoadUserListingFromLocal() throws Exception {
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(localStreamSearchRepository).getStreamsListing(anyString(), anyArray());
  }

  @Test public void shouldLoadUserListingFromRemote() throws Exception {
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(remoteStreamSearchRepository).getStreamsListing(anyString(), anyArray());
  }

  @Test public void shouldLoadFavoritesFromLocal() throws Exception {
    when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(localStreamRepository).getStreamsByIds(anyList(), anyArray());
  }

  @Test public void shouldLoadFavoritesFromRemote() throws Exception {
    when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(remoteStreamRepository).getStreamsByIds(anyList(), anyArray());
  }

  @Test public void shouldReturnListingWithoutHoldingIfUserHaveNoHoldingStreams() throws Exception {
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getHoldingStreams(), Collections.emptyList());
  }

  @Test public void shouldReturnListingWithoutFavoritesIfUserHaveNoFavoriteStreams()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getFavoritedStreams(), Collections.emptyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamRepositoryThrowServerComunicationException()
      throws Exception {
    when(remoteFavoriteRepository.getFavorites(ID_USER)).thenReturn(favorites());
    doThrow(ServerCommunicationException.class).
        when(remoteStreamRepository).getStreamsByIds(anyList(), anyArray());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private Map<String, Integer> holderWatchers() {
    Map<String, Integer> map = new HashMap<>();
    for (Stream stream : favoriteStreams()) {
      map.put(stream.getId(), 0);
    }
    return map;
  }

  private List<StreamSearchResult> favoriteStreamResults() {
    List<StreamSearchResult> streamSearchResults = new ArrayList<>();
    for (Stream stream : favoriteStreams()) {
      StreamSearchResult streamSearchResult = new StreamSearchResult();
      streamSearchResult.setStream(stream);
      streamSearchResults.add(streamSearchResult);
    }
    return streamSearchResults;
  }

  private List<Stream> favoriteStreams() {
    List<Stream> streams = new ArrayList<>();
    for (Favorite favorite : favorites()) {
      Stream stream = new Stream();
      stream.setId(favorite.getIdStream());
      streams.add(stream);
    }
    return streams;
  }

  private List<StreamSearchResult> listingStreams() {
    List<StreamSearchResult> streamSearchResults = new ArrayList<>();
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    Stream stream = new Stream();
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

  private String[] anyArray() {
    return any(String[].class);
  }
}
