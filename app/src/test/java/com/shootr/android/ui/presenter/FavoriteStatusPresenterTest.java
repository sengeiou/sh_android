package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.event.GetFavoriteStatusInteractor;
import com.shootr.android.domain.interactor.event.RemoveFromFavoritesInteractor;
import com.shootr.android.ui.views.FavoriteStatusView;
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

public class FavoriteStatusPresenterTest {

    private static final String STUB_STREAM_ID = "stream_id";

    @Mock GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock FavoriteStatusView favoriteStatusView;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;

    private FavoriteStatusPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new FavoriteStatusPresenter(getFavoriteStatusInteractor,
          addToFavoritesInteractor,
          removeFromFavoritesInteractor);
        presenter.setView(favoriteStatusView);
    }

    @Test
    public void shouldShowRemoveFromFavoritesButtonWhenInitializedIfIsFavorite() throws Exception {
        setupFavoriteStatusCallbacks(true);

        presenter.initialize(favoriteStatusView, STUB_STREAM_ID);

        verify(favoriteStatusView).showRemoveFromFavoritesButton();
    }

    @Test
    public void shouldShowAddToFavoritesButtonWhenInitializeIfStreamIsNotFavorite() throws Exception {
        setupFavoriteStatusCallbacks(false);

        presenter.initialize(favoriteStatusView, STUB_STREAM_ID);

        verify(favoriteStatusView).showAddToFavoritesButton();
    }

    @Test
    public void shouldHideAddToFavoritesButtonWhenAddToFavorite() throws Exception {
        setupAddToFavoriteCallbacks();

        presenter.addToFavorites();

        verify(favoriteStatusView).hideAddToFavoritesButton();
    }

    @Test
    public void shouldShowRemoveFromFavoritesButtonWhenAddToFavorites() throws Exception {
        setupAddToFavoriteCallbacks();

        presenter.addToFavorites();

        verify(favoriteStatusView).showRemoveFromFavoritesButton();
    }

    @Test
    public void shouldHideRemoveFromFavoritesButtonWhenRemoveFromFavorites() throws Exception {
        setupRemoveFromFavoriteCallbacks();

        presenter.removeFromFavorites();

        verify(favoriteStatusView).hideRemoveFromFavoritesButton();
    }

    @Test
    public void shouldShowAddToFavoritesButtonWhenRemoveFromFavorites() throws Exception {
        setupRemoveFromFavoriteCallbacks();

        presenter.removeFromFavorites();

        verify(favoriteStatusView).showAddToFavoritesButton();
    }

    private void setupRemoveFromFavoriteCallbacks() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
                return null;
            }
        }).when(removeFromFavoritesInteractor).removeFromFavorites(anyString(), any(Interactor.CompletedCallback.class));
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