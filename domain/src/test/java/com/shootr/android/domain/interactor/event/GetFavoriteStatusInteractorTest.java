package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetFavoriteStatusInteractorTest {

    public static final String EVENT_ID = "event_id";

    @Mock Interactor.Callback<Boolean> callback;
    @Mock FavoriteRepository localFavoriteRepository;

    private GetFavoriteStatusInteractor getFavoriteStatusInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getFavoriteStatusInteractor = new GetFavoriteStatusInteractor(interactorHandler, postExecutionThread, localFavoriteRepository);
    }

    @Test
    public void shouldCallbackWhenLoadFavoriteEvents() {
        getFavoriteStatusInteractor.loadFavoriteStatus(EVENT_ID, callback);
        verify(callback).onLoaded(anyBoolean());
    }

    @Test
    public void shouldLoadLocalEventStatus() {
        when(localFavoriteRepository.getFavoriteByEvent(EVENT_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(EVENT_ID, callback);
        verify(callback).onLoaded(anyBoolean());
    }

    @Test
    public void shouldCallbackTrueIfEventIsFavorited() {
        when(localFavoriteRepository.getFavoriteByEvent(EVENT_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(EVENT_ID, callback);
        verify(callback).onLoaded(true);
    }

    @Test
    public void shouldCallbackFalseIfEventIsNotFavorited() {
        when(localFavoriteRepository.getFavoriteByEvent(EVENT_ID)).thenReturn(null);
        getFavoriteStatusInteractor.loadFavoriteStatus(EVENT_ID, callback);
        verify(callback).onLoaded(false);
    }

    @Test
    public void shouldLoadLocalEventStatusBeforeCallback() {
        when(localFavoriteRepository.getFavoriteByEvent(EVENT_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(EVENT_ID, callback);

        InOrder inOrder = inOrder(callback, localFavoriteRepository);
        inOrder.verify(localFavoriteRepository).getFavoriteByEvent(anyString());
        inOrder.verify(callback).onLoaded(anyBoolean());
    }
}
