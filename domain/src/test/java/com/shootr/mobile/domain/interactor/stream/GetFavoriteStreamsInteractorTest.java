package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Favorite;
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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFavoriteStreamsInteractorTest {

    public static final String STREAM_ID = "stream_id";
    private static final String STREAM_ID_0 = "id_0";
    private static final String STREAM_ID_1 = "id_1";
    private static final String STREAM_ID_2 = "id_2";
    public static final String USER_ID = "userId";

    @Mock Interactor.Callback<List<StreamSearchResult>> callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    @Mock com.shootr.mobile.domain.repository.WatchersRepository watchersRepository;
    @Mock SessionRepository sessionRepository;
    @Spy SpyCallback<List<StreamSearchResult>> spyCallback = new SpyCallback<>();

    private GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        setupCurrentUserWatching();
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getFavoriteStreamsInteractor = new GetFavoriteStreamsInteractor(interactorHandler, postExecutionThread,
          localFavoriteRepository,
          remoteFavoriteRepository, localStreamRepository, watchersRepository, sessionRepository);
    }

    @Test
    public void shouldCallbackWhenLoadFavoriteStreams(){
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(callback, atLeastOnce()).onLoaded(anyList());
    }

    @Test
    public void shouldLoadFavoritesFromLocal(){
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(localFavoriteRepository).getFavorites(anyString());
    }

    @Test
    public void shouldLoadFavoritesFromRemote(){
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(remoteFavoriteRepository).getFavorites(anyString());
    }

    @Test
    public void shouldLoadStreamsFromLocal(){
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(localStreamRepository, atLeastOnce()).getStreamsByIds(anyList());
    }

    @Test
    public void shouldLoadWatchers(){
        when(localStreamRepository.getStreamsByIds(anyList())).thenReturn(listWithOneStreams());
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(watchersRepository, atLeastOnce()).getWatchers();
    }

    @Test
    public void shouldCallbackStreamsSortedByName() throws Exception {
        setupStreamRepositoryReturnsStreamsWithInputIds();
        when(localFavoriteRepository.getFavorites(USER_ID)).thenReturn(unorderedFavorites());
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);

        getFavoriteStreamsInteractor.loadFavoriteStreams(spyCallback);
        List<StreamSearchResult> results = spyCallback.firstResult();

        assertThat(results).containsSequence(orderedStreamResults());
    }

    @Test
    public void shouldLoadLocalStreamsFromFavorites() {
        when(localFavoriteRepository.getFavorites(USER_ID)).thenReturn(listWithOneFavorite());
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        getFavoriteStreamsInteractor.loadFavoriteStreams(callback);
        verify(localStreamRepository).getStreamsByIds(favoriteStreamsIds());
    }


    private List<Favorite> unorderedFavorites() {
        return Arrays.asList(
          favorite(STREAM_ID_2, 2),
          favorite(STREAM_ID_0, 0),
          favorite(STREAM_ID_1, 1)
        );
    }

    private StreamSearchResult[] orderedStreamResults() {
        return new StreamSearchResult[] {
          streamResult(STREAM_ID_0), streamResult(STREAM_ID_1), streamResult(STREAM_ID_2)
        };
    }

    private StreamSearchResult streamResult(String streamId) {
        return new StreamSearchResult(stream(streamId), 0);
    }

    private com.shootr.mobile.domain.Stream stream(String streamId) {
        com.shootr.mobile.domain.Stream stream = new com.shootr.mobile.domain.Stream();
        stream.setId(streamId);
        stream.setTitle("title_"+streamId);
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

    private List<com.shootr.mobile.domain.Stream> listWithOneStreams() {
        List<com.shootr.mobile.domain.Stream> streams = new ArrayList<>();
        com.shootr.mobile.domain.Stream stream = new com.shootr.mobile.domain.Stream();
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
        when(sessionRepository.getCurrentUser()).thenReturn(userWatching());
    }

    protected void setupStreamRepositoryReturnsStreamsWithInputIds() {
        when(localStreamRepository.getStreamsByIds(anyListOf(String.class))).thenAnswer(new Answer<List<com.shootr.mobile.domain.Stream>>() {
            @Override
            public List<com.shootr.mobile.domain.Stream> answer(InvocationOnMock invocation) throws Throwable {
                List<String> ids = (List<String>) invocation.getArguments()[0];
                List<com.shootr.mobile.domain.Stream> streams = new ArrayList<com.shootr.mobile.domain.Stream>();
                for (String id : ids) {
                    streams.add(stream(id));
                }
                return streams;
            }
        });
    }
}
