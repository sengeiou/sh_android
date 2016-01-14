package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStatusInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.util.ErrorMessageFactory;
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

public class StreamTimelineOptionsPresenterTest {

    private static final String STUB_STREAM_ID = "stream_id";

    @Mock GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock StreamTimelineOptionsView streamTimelineOptionsView;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock GetMutedStreamsInteractor getMutedStreamsInteractor;
    @Mock MuteInteractor muteInteractor;
    @Mock UnmuteInteractor unmuteInteractor;

    private StreamTimelineOptionsPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new StreamTimelineOptionsPresenter(getFavoriteStatusInteractor,
          addToFavoritesInteractor,
          removeFromFavoritesInteractor,
          getMutedStreamsInteractor,
          muteInteractor,
          unmuteInteractor,
          errorMessageFactory);
        presenter.setView(streamTimelineOptionsView);
    }

    @Test
    public void shouldShowRemoveFromFavoritesButtonWhenInitializedIfIsFavorite() throws Exception {
        setupFavoriteStatusCallbacks(true);

        presenter.initialize(streamTimelineOptionsView, STUB_STREAM_ID);

        verify(streamTimelineOptionsView).showRemoveFromFavoritesButton();
    }

    @Test
    public void shouldShowAddToFavoritesButtonWhenInitializeIfStreamIsNotFavorite() throws Exception {
        setupFavoriteStatusCallbacks(false);

        presenter.initialize(streamTimelineOptionsView, STUB_STREAM_ID);

        verify(streamTimelineOptionsView).showAddToFavoritesButton();
    }

    @Test
    public void shouldHideAddToFavoritesButtonWhenAddToFavorite() throws Exception {
        setupAddToFavoriteCallbacks();

        presenter.addToFavorites();

        verify(streamTimelineOptionsView).hideAddToFavoritesButton();
    }

    @Test
    public void shouldShowRemoveFromFavoritesButtonWhenAddToFavorites() throws Exception {
        setupAddToFavoriteCallbacks();

        presenter.addToFavorites();

        verify(streamTimelineOptionsView).showRemoveFromFavoritesButton();
    }

    @Test
    public void shouldHideRemoveFromFavoritesButtonWhenRemoveFromFavorites() throws Exception {
        setupRemoveFromFavoriteCallbacks();

        presenter.removeFromFavorites();

        verify(streamTimelineOptionsView).hideRemoveFromFavoritesButton();
    }

    @Test
    public void shouldShowAddToFavoritesButtonWhenRemoveFromFavorites() throws Exception {
        setupRemoveFromFavoriteCallbacks();

        presenter.removeFromFavorites();

        verify(streamTimelineOptionsView).showAddToFavoritesButton();
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
        }).when(addToFavoritesInteractor).addToFavorites(anyString(), any(Interactor.CompletedCallback.class), any(
          Interactor.ErrorCallback.class));
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