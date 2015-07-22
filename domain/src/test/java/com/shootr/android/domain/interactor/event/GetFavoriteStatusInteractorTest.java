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

    public static final String STREAM_ID = "stream_id";

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
    public void shouldCallbackWhenLoadFavoriteStreams() {
        getFavoriteStatusInteractor.loadFavoriteStatus(STREAM_ID, callback);
        verify(callback).onLoaded(anyBoolean());
    }

    @Test
    public void shouldLoadLocalStreamStatus() {
        when(localFavoriteRepository.getFavoriteByStream(STREAM_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(STREAM_ID, callback);
        verify(callback).onLoaded(anyBoolean());
    }

    @Test
    public void shouldCallbackTrueIfStreamIsFavorited() {
        when(localFavoriteRepository.getFavoriteByStream(STREAM_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(STREAM_ID, callback);
        verify(callback).onLoaded(true);
    }

    @Test
    public void shouldCallbackFalseIfStreamIsNotFavorited() {
        when(localFavoriteRepository.getFavoriteByStream(STREAM_ID)).thenReturn(null);
        getFavoriteStatusInteractor.loadFavoriteStatus(STREAM_ID, callback);
        verify(callback).onLoaded(false);
    }

    @Test
    public void shouldLoadLocalStreamStatusBeforeCallback() {
        when(localFavoriteRepository.getFavoriteByStream(STREAM_ID)).thenReturn(new Favorite());
        getFavoriteStatusInteractor.loadFavoriteStatus(STREAM_ID, callback);

        InOrder inOrder = inOrder(callback, localFavoriteRepository);
        inOrder.verify(localFavoriteRepository).getFavoriteByStream(anyString());
        inOrder.verify(callback).onLoaded(anyBoolean());
    }
}
