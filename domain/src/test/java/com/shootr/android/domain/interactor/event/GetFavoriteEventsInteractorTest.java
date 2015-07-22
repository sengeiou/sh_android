package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.SpyCallback;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.WatchersRepository;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFavoriteEventsInteractorTest {

    public static final String EVENT_ID = "event_id";
    private static final String EVENT_ID_0 = "id_0";
    private static final String EVENT_ID_1 = "id_1";
    private static final String EVENT_ID_2 = "id_2";

    @Mock Interactor.Callback<List<EventSearchResult>> callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock StreamRepository localStreamRepository;
    @Mock WatchersRepository watchersRepository;
    @Spy SpyCallback<List<EventSearchResult>> spyCallback = new SpyCallback<>();

    private GetFavoriteEventsInteractor getFavoriteEventsInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getFavoriteEventsInteractor = new GetFavoriteEventsInteractor(interactorHandler, postExecutionThread,
          localFavoriteRepository,
          remoteFavoriteRepository, localStreamRepository, watchersRepository);
    }

    @Test
    public void shouldCallbackWhenLoadFavoriteEvents(){
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(callback, atLeastOnce()).onLoaded(anyList());
    }

    @Test
    public void shouldLoadFavoritesFromLocal(){
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(localFavoriteRepository).getFavorites();
    }

    @Test
    public void shouldLoadFavoritesFromRemote(){
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(remoteFavoriteRepository).getFavorites();
    }

    @Test
    public void shouldLoadEventsFromLocal(){
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(localStreamRepository, atLeastOnce()).getStreamsByIds(anyList());
    }

    @Test
    public void shouldLoadWatchers(){
        when(localStreamRepository.getStreamsByIds(anyList())).thenReturn(listWithOneEvent());
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(watchersRepository, atLeastOnce()).getWatchers();
    }

    @Test
    public void shouldCallbackEventsSortedByName() throws Exception {
        setupEventRepositoryReturnsEventsWithInputIds();
        when(localFavoriteRepository.getFavorites()).thenReturn(unorderedFavorites());

        getFavoriteEventsInteractor.loadFavoriteEvents(spyCallback);
        List<EventSearchResult> results = spyCallback.firstResult();

        assertThat(results).containsSequence(orderedEventResults());
    }

    @Test
    public void shouldLoadLocalEventsFromFavorites() {
        when(localFavoriteRepository.getFavorites()).thenReturn(listWithOneFavorite());
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(localStreamRepository).getStreamsByIds(favoriteEventsIds());
    }


    private List<Favorite> unorderedFavorites() {
        return Arrays.asList(
          favorite(EVENT_ID_2, 2),
          favorite(EVENT_ID_0, 0),
          favorite(EVENT_ID_1, 1)
        );
    }

    private EventSearchResult[] orderedEventResults() {
        return new EventSearchResult[] {
          eventResult(EVENT_ID_0), eventResult(EVENT_ID_1), eventResult(EVENT_ID_2)
        };
    }

    private EventSearchResult eventResult(String eventId) {
        return new EventSearchResult(event(eventId), 0);
    }

    private Stream event(String eventId) {
        Stream stream = new Stream();
        stream.setId(eventId);
        stream.setTitle("title_"+eventId);
        return stream;
    }

    private Favorite favorite(String eventId, int order) {
        Favorite favorite = new Favorite();
        favorite.setIdEvent(eventId);
        favorite.setOrder(order);
        return favorite;
    }

    private List<Favorite> listWithOneFavorite() {
        ArrayList<Favorite> favorites = new ArrayList<>();
        Favorite favorite = new Favorite();
        favorite.setIdEvent(EVENT_ID);
        favorite.setOrder(0);
        favorites.add(favorite);
        return favorites;
    }

    private List<String> favoriteEventsIds() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(EVENT_ID);
        return strings;
    }

    private List<Stream> listWithOneEvent() {
        List<Stream> streams = new ArrayList<>();
        Stream stream = new Stream();
        stream.setId(EVENT_ID);
        streams.add(stream);
        return streams;
    }

    protected void setupEventRepositoryReturnsEventsWithInputIds() {
        when(localStreamRepository.getStreamsByIds(anyListOf(String.class))).thenAnswer(new Answer<List<Stream>>() {
            @Override
            public List<Stream> answer(InvocationOnMock invocation) throws Throwable {
                List<String> ids = (List<String>) invocation.getArguments()[0];
                List<Stream> streams = new ArrayList<Stream>();
                for (String id : ids) {
                    streams.add(event(id));
                }
                return streams;
            }
        });
    }
}
