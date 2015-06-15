package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

public class GetFavoriteEventsInteractorTest {

    @Mock Interactor.Callback<List<EventSearchResult>> callback;

    private GetFavoriteEventsInteractor getFavoriteEventsInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getFavoriteEventsInteractor = new GetFavoriteEventsInteractor(interactorHandler, postExecutionThread);
    }

    @Test
    public void shouldCallbackWhenLoadFavoriteEvents(){
        getFavoriteEventsInteractor.loadFavoriteEvents(callback);
        verify(callback).onLoaded(anyList());
    }
}
