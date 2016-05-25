package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Favorite;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamMode;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFavoriteStreamsInteractorTest {

  public static final String STREAM_ID = "stream";
  public static final String USER_ID = "userId";
  private static final String STREAM_ID_0 = "id_0";
  private static final String STREAM_ID_1 = "id_1";
  private static final String STREAM_ID_2 = "id_2";
  @Mock Interactor.Callback<List<StreamSearchResult>> callback;
  @Mock FavoriteRepository localFavoriteRepository;
  @Mock FavoriteRepository remoteFavoriteRepository;
  @Mock StreamRepository localStreamRepository;
  @Mock WatchersRepository watchersRepository;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Spy SpyCallback<List<StreamSearchResult>> spyCallback = new SpyCallback<>();

  private GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    setupCurrentUserWatching();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getFavoriteStreamsInteractor =
        new GetFavoriteStreamsInteractor(interactorHandler, postExecutionThread,
            localFavoriteRepository, remoteFavoriteRepository, localStreamRepository,
            watchersRepository, sessionRepository, localUserRepository);
  }

  @Test public void shouldCallbackWhenLoadFavoriteStreams() {
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(callback, atLeastOnce()).onLoaded(anyList());
  }

  @Test public void shouldLoadFavoritesFromLocal() {
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(localFavoriteRepository).getFavorites(anyString());
  }

  @Test public void shouldLoadFavoritesFromRemote() {
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(remoteFavoriteRepository).getFavorites(anyString());
  }

  @Test public void shouldLoadStreamsFromLocal() {
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(localStreamRepository, atLeastOnce()).getStreamsByIds(anyList(), anyArray());
  }

  @Test public void shouldLoadWatchers() {
    when(localStreamRepository.getStreamsByIds(anyList(), anyArray())).thenReturn(
        listWithOneStreams());
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(watchersRepository, atLeastOnce()).getWatchers();
  }

  @Test public void shouldCallbackStreamsSortedByName() throws Exception {
    setupStreamRepositoryReturnsStreamsWithInputIds();
    when(localFavoriteRepository.getFavorites(USER_ID)).thenReturn(unorderedFavorites());
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);

    getFavoriteStreamsInteractor.loadFavoriteStreams(spyCallback);
    List<StreamSearchResult> results = spyCallback.firstResult();

    assertThat(results).containsSequence(orderedStreamResults());
  }

  @Test public void shouldLoadLocalStreamsFromFavorites() {
    when(localFavoriteRepository.getFavorites(USER_ID)).thenReturn(listWithOneFavorite());
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
    verify(localStreamRepository).getStreamsByIds(favoriteStreamsIds(), StreamMode.TYPES_STREAM);
  }

  @Test public void shouldCallbackWhenLoadFavoriteStreamsFromLocalOnly() {
    getFavoriteStreamsInteractor.loadFavoriteStreamsFromLocalOnly(callback);
    verify(callback, atLeastOnce()).onLoaded(anyList());
  }

  private List<Favorite> unorderedFavorites() {
    return Arrays.asList(favorite(STREAM_ID_2, 2), favorite(STREAM_ID_0, 0),
        favorite(STREAM_ID_1, 1));
  }

  private StreamSearchResult[] orderedStreamResults() {
    return new StreamSearchResult[] {
        streamResult(STREAM_ID_0), streamResult(STREAM_ID_1), streamResult(STREAM_ID_2)
    };
  }

  private StreamSearchResult streamResult(String streamId) {
    return new StreamSearchResult(stream(streamId), 0);
  }

  private Stream stream(String streamId) {
    Stream stream = new Stream();
    stream.setId(streamId);
    stream.setTitle("title_" + streamId);
    return stream;
  }

  private Favorite favorite(String streamId, int order) {
    Favorite favorite = new Favorite();
    favorite.setIdStream(streamId);
    favorite.setOrder(order);
    return favorite;
  }

  private List<Favorite> listWithOneFavorite() {
    ArrayList<Favorite> favorites = new ArrayList<>();
    Favorite favorite = new Favorite();
    favorite.setIdStream(STREAM_ID);
    favorite.setOrder(0);
    favorites.add(favorite);
    return favorites;
  }

  private List<String> favoriteStreamsIds() {
    ArrayList<String> strings = new ArrayList<>();
    strings.add(STREAM_ID);
    return strings;
  }

  private List<Stream> listWithOneStreams() {
    List<Stream> streams = new ArrayList<>();
    Stream stream = new Stream();
    stream.setId(STREAM_ID);
    streams.add(stream);
    return streams;
  }

  private User userWatching() {
    User user = new User();
    user.setIdUser(USER_ID);
    user.setIdWatchingStream("stream");
    return user;
  }

  private void setupCurrentUserWatching() {
    when(localUserRepository.getUserById(anyString())).thenReturn(userWatching());
  }

  protected void setupStreamRepositoryReturnsStreamsWithInputIds() {
    when(localStreamRepository.getStreamsByIds(anyListOf(String.class), anyArray())).thenAnswer(
        new Answer<List<Stream>>() {
          @Override public List<Stream> answer(InvocationOnMock invocation) throws Throwable {
            List<String> ids = (List<String>) invocation.getArguments()[0];
            List<Stream> streams = new ArrayList<Stream>();
            for (String id : ids) {
              streams.add(stream(id));
            }
            return streams;
          }
        });
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}
