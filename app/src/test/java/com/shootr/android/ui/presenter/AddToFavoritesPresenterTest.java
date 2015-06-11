package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.event.GetFavoriteStatusInteractor;
import com.shootr.android.ui.views.AddToFavoritesView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class AddToFavoritesPresenterTest {

    private static final String STUB_EVENT_ID = "event_id";

    @Mock GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock AddToFavoritesView addToFavoritesView;

    private AddToFavoritesPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new AddToFavoritesPresenter(getFavoriteStatusInteractor, addToFavoritesInteractor);
        presenter.setView(addToFavoritesView);
    }

    @Test
    public void shouldHideAddToFavoritesButtonWhenInitializeIfEventIsFavorite() throws Exception {
        setupFavoriteStatusCallbacks(true);

        presenter.initialize(addToFavoritesView, STUB_EVENT_ID);

        verify(addToFavoritesView).hideAddToFavoritesButton();
    }

    @Test
    public void shouldShowAddToFavoritesButtonWhenInitializeIfEventIsNotFavorite() throws Exception {
        setupFavoriteStatusCallbacks(false);

        presenter.initialize(addToFavoritesView, STUB_EVENT_ID);

        verify(addToFavoritesView).showAddToFavoritesButton();
    }

    @Test
    public void shouldHideAddToFavoritesButtonWhenAddToFavorite() throws Exception {
        setupAddToFavoriteCallbacks();

        presenter.addToFavorites();

        verify(addToFavoritesView).hideAddToFavoritesButton();
    }

    private void setupAddToFavoriteCallbacks() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
                return null;
            }
        }).when(addToFavoritesInteractor).addToFavorites(anyString(), any(Interactor.CompletedCallback.class));
    }

    private void setupFavoriteStatusCallbacks(final boolean isFavorite) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback) invocation.getArguments()[1]).onLoaded(isFavorite);
                return null;
            }
        }).when(getFavoriteStatusInteractor).loadFavoriteStatus(anyString(), any(Interactor.Callback.class));
    }
}