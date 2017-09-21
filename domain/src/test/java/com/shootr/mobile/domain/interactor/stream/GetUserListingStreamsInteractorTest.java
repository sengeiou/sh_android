package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.Followable;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Follows;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.Listing;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserListingStreamsInteractorTest {

  public static final String ID_USER = "idUser";
  public static final String ID_STREAM = "idStream";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock InternalStreamSearchRepository localStreamSearchRepository;
  @Mock StreamSearchRepository remoteStreamSearchRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock ExternalStreamRepository remoteStreamRepository;
  @Mock InternalFavoriteRepository localFavoriteRepository;
  @Mock FollowRepository remoteFollowRepository;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock SessionRepository sessionRepository;
  @Spy SpyCallback<Listing> spyCallback = new SpyCallback<>();
  private GetUserListingStreamsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor = new GetUserListingStreamsInteractor(interactorHandler, postExecutionThread,
        localStreamSearchRepository, remoteStreamSearchRepository, remoteFollowRepository);
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
  }

  @Test public void shouldReturnListingWithHoldingIfUserHaveHoldingStreams() throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getHoldingStreams(), listingStreams());
  }

  @Test public void shouldReturnListingWithFavoritesIfUserHaveFavoriteStreams() throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteStreamRepository.getStreamsByIds(anyList(), anyArray())).thenReturn(
        favoriteStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getFavoritedStreams(), favoriteStreamResults());
  }

  @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHaveHoldingStreams()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesHolding(), true);
  }

  @Test public void shouldReturnListingWithIncludeFavoritesIfUserHaveFavoriteStreams()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteStreamRepository.getStreamsByIds(anyList(), anyArray())).thenReturn(
        favoriteStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesFavorited(), true);
  }

  @Test public void shouldReturnListingWithIncludeHoldingTrueIfUserHoldingStreamsAreEmptyList()
      throws Exception {
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesHolding(), true);
  }

  @Test public void shouldReturnListingWithIncludeFavoritesTrueIfUserFavoriteStreamsAreEmptyList()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.includesFavorited(), true);
  }

  @Test public void shouldLoadUserListingFromRemote() throws Exception {
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(remoteStreamSearchRepository).getStreamsListing(anyString(), anyArray());
  }

  @Test public void shouldLoadFavoritesFromRemote() throws Exception {
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(remoteFollowRepository).getFollowing(anyString(), anyArray(), anyLong());
  }

  @Test public void shouldReturnListingWithoutHoldingIfUserHaveNoHoldingStreams() throws Exception {
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(follow());
    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getHoldingStreams(), Collections.emptyList());
  }

  @Test public void shouldReturnListingWithoutFavoritesIfUserHaveNoFavoriteStreams()
      throws Exception {
    when(remoteStreamSearchRepository.getStreamsListing(ID_USER, TYPES_STREAM)).thenReturn(
        listingStreams());
    when(remoteFollowRepository.getFollowing(ID_USER, new String[] { FollowableType.STREAM }, null))
        .thenReturn(followEmpty());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);
    Listing listing = spyCallback.lastResult();

    assertEquals(listing.getFavoritedStreams(), Collections.emptyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamRepositoryThrowServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).
        when(remoteFollowRepository).getFollowing(anyString(), anyArray(), anyLong());

    interactor.loadUserListingStreams(spyCallback, errorCallback, ID_USER);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
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
    for (Followable follow : follow().getData()) {
      Stream stream = new Stream();
      stream.setId(((Stream) follow).getId());
      stream.setAuthorId(ID_USER);
      streams.add(stream);
    }
    return streams;
  }

  private Follows follow() {
    Follows follows = new Follows();
    Stream stream = new Stream();
    stream.setAuthorId(ID_USER);
    stream.setId(ID_STREAM);
    stream.setRemoved(false);
    List<Followable> data = new ArrayList<>();
    data.add(stream);
    follows.setData(data);
    return follows;
  }

  private Follows followEmpty() {
    Follows follows = new Follows();
    List<Followable> data = new ArrayList<>();
    follows.setData(data);
    return follows;
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
