package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.bus.FavoriteAdded;
import com.shootr.android.domain.bus.UnwatchDone;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter, FavoriteAdded.Receiver, UnwatchDone.Receiver{

    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final UnwatchStreamInteractor unwatchStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;

    @Inject public FavoritesListPresenter(GetFavoriteStreamsInteractor getFavoriteStreamsInteractor,
      ShareStreamInteractor shareStreamInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor,
      StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory,
      @Main Bus bus) {
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.unwatchStreamInteractor = unwatchStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.bus = bus;
    }

    public void setView(FavoritesListView favoritesListView) {
        this.favoritesListView = favoritesListView;
    }

    public void initialize(FavoritesListView favoritesListView) {
        setView(favoritesListView);
        favoritesListView.showLoading();
        this.loadFavorites();
    }

    protected void loadFavorites() {
        getFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override
            public void onLoaded(List<StreamSearchResult> streams) {
                favoritesListView.hideLoading();
                if (streams.isEmpty()) {
                    favoritesListView.showEmpty();
                    favoritesListView.hideContent();
                } else {
                    List<StreamResultModel> streamModels = streamResultModelMapper.transform(streams);
                    favoritesListView.renderFavorites(streamModels);
                    favoritesListView.showContent();
                    favoritesListView.hideEmpty();
                }
            }
        });
    }


    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(),
          stream.getStreamModel().getTitle(),
          stream.getStreamModel().isRemoved(),
          stream.getStreamModel().getAuthorId());
    }

    private void selectStream(final String idStream, String streamTitle, Boolean removed, String authorId) {
        favoritesListView.navigateToStreamTimeline(idStream, streamTitle, authorId);
    }

    @Override
    public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.loadFavorites();
        }
    }

    @Override
    public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    @Subscribe
    @Override
    public void onFavoriteAdded(FavoriteAdded.Event event) {
        loadFavorites();
    }


    public void shareStream(StreamResultModel stream) {
        shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favoritesListView.showStreamShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        } else {
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        favoritesListView.showError(errorMessage);
    }

    public void removeFromFavorites(StreamResultModel stream) {
        removeFromFavoritesInteractor.removeFromFavorites(stream.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override
              public void onCompleted() {
                  loadFavorites();
              }
          });
    }

    public void unwatchStream() {
        unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                loadFavorites();
            }
        });
    }

    @Override
    @Subscribe
    public void onUnwatchDone(UnwatchDone.Event event) {
        loadFavorites();
    }
}
