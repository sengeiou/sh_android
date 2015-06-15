package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteEventsInteractor;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class FavoritesListPresenterTest {

    @Mock FavoritesListView favoritesListView;
    @Mock GetFavoriteEventsInteractor getFavoriteEventsInteractor;

    private FavoritesListPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new FavoritesListPresenter(getFavoriteEventsInteractor);
    }

    @Test
    public void shouldLoadFavoritesWhenInitialized() throws Exception {
        presenter.initialize(favoritesListView);

        verify(getFavoriteEventsInteractor).loadFavoriteEvents(anyCallback());
    }

    @Test
    public void shouldShowLoadingWhenInitialized() throws Exception {
        presenter.initialize(favoritesListView);

        verify(favoritesListView).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenInitializedIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacks(stubResult());

        presenter.initialize(favoritesListView);

        verify(favoritesListView).hideLoading();
    }

    @Test
    public void shouldShowEmptyWhenInitializedIfInteractorCallbacksEmpty() throws Exception {
        setupInteractorCallbacks(empty());

        presenter.initialize(favoritesListView);

        verify(favoritesListView).showEmpty();
    }

    private Event stubEvent() {
        return new Event();
    }

    private List<Event> stubResult() {
        return Arrays.asList(stubEvent());
    }

    private List<Event> empty() {
        return Collections.EMPTY_LIST;
    }

    protected Interactor.Callback<List<Event>> anyCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupInteractorCallbacks(final List<Event> result) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Event>> callback = (Interactor.Callback) invocation.getArguments()[0];
                callback.onLoaded(result);
                return null;
            }
        }).when(getFavoriteEventsInteractor).loadFavoriteEvents(anyCallback());
    }
}