package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
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
    @Mock GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    @Mock StreamResultModelMapper streamResultModelMapper;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock StreamModelMapper streamModelMapper;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock Bus bus;

    private FavoritesListPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new FavoritesListPresenter(getFavoriteStreamsInteractor, shareStreamInteractor,
          removeFromFavoritesInteractor,
          streamResultModelMapper, errorMessageFactory, bus);
        presenter.setView(favoritesListView);
    }

    @Test
    public void shouldLoadFavoritesWhenInitialized() throws Exception {
        presenter.initialize(favoritesListView);

        verify(getFavoriteStreamsInteractor).loadFavoriteStreams(anyCallback());
    }

    @Test
    public void shouldLoadFavoritesWhenPausedAndResumed() throws Exception {
        presenter.pause();
        presenter.resume();

        verify(getFavoriteStreamsInteractor).loadFavoriteStreams(anyCallback());
    }

    @Test
    public void shouldShowLoadingWhenLoadFavorites() throws Exception {
        presenter.loadFavorites();

        verify(favoritesListView).showLoading();
    }

    @Test
    public void shouldShowContentWhenLoadFavoritesIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacks(stubResult());

        presenter.loadFavorites();

        verify(favoritesListView).showContent();
    }

    @Test
    public void shouldHideContentWhenLoadFavoritesIfInteractorCallbacksEmpty() throws Exception {
        setupInteractorCallbacks(empty());

        presenter.loadFavorites();

        verify(favoritesListView).hideContent();
    }

    @Test
    public void shouldHideLoadingWhenLoadFavoritesIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacks(stubResult());

        presenter.loadFavorites();

        verify(favoritesListView).hideLoading();
    }

    @Test
    public void shouldShowEmptyWhenLoadFavoritesIfInteractorCallbacksEmpty() throws Exception {
        setupInteractorCallbacks(empty());

        presenter.loadFavorites();

        verify(favoritesListView).showEmpty();
    }

    @Test
    public void shouldHideEmptyWhenLoadFavoritesIfInteractorCallbacksResults() throws Exception {
        setupInteractorCallbacks(stubResult());

        presenter.loadFavorites();

        verify(favoritesListView).hideEmpty();
    }

    @Test
    public void shouldShowFavoritesWhenLoadFavoritesIfInteractorCallbacksResults() throws Exception {
        setupInteractorCallbacks(stubResult());

        presenter.loadFavorites();

        verify(favoritesListView).renderFavorites(stubResultModel());
    }

    private StreamSearchResult stubStream() {
        return new StreamSearchResult();
    }

    private List<StreamSearchResult> stubResult() {
        return Arrays.asList(stubStream());
    }

    private List<StreamResultModel> stubResultModel() {
        return streamResultModelMapper.transform(stubResult());
    }

    private List<StreamSearchResult> empty() {
        return Collections.EMPTY_LIST;
    }

    protected Interactor.Callback<List<StreamSearchResult>> anyCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupInteractorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<StreamSearchResult>> callback = (Interactor.Callback) invocation.getArguments()[0];
                callback.onLoaded(result);
                return null;
            }
        }).when(getFavoriteStreamsInteractor).loadFavoriteStreams(anyCallback());
    }
}