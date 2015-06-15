package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFavoriteEventsInteractorTest {

    public static final String EVENT_ID = "event_id";
    @Mock Interactor.Callback<List<EventSearchResult>> callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock EventRepository localEventRepository;
    @Mock WatchersRepository watchersRepository;

    private GetFavoriteEventsInteractor getFavoriteEventsInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getFavoriteEventsInteractor = new GetFavoriteEventsInteractor(interactorHandler, postExecutionThread,
          localFavoriteRepository,
          remoteFavoriteRepository,
          localEventRepository, watchersRepository);
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
        verify(localEventRepository, atLeastOnce()).getEventsByIds(anyList());
    }

    @Test
    public void shouldLoadWatchers(){
        when(localEventRepository.getEventsByIds(anyList())).thenReturn(listWithOneEvent());
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(watchersRepository, atLeastOnce()).getWatchers(anyString());
    }

    private List<Event> listWithOneEvent() {
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setId(EVENT_ID);
        events.add(event);
        return events;
    }
}
