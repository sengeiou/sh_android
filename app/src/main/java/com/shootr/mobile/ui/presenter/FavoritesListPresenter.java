package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.bus.FavoriteAdded;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.FavoritesListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter, FavoriteAdded.Receiver, UnwatchDone.Receiver, StreamMuted.Receiver{

    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final UnwatchStreamInteractor unwatchStreamInteractor;
    private final GetMutedStreamsInteractor getMutedStreamsInteractor;
    private final MuteInteractor muteInteractor;
    private final UnmuteInteractor unmuteInterator;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;
    private List<String> mutedStreamIds;

    @Inject public FavoritesListPresenter(GetFavoriteStreamsInteractor getFavoriteStreamsInteractor,
      ShareStreamInteractor shareStreamInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor, GetMutedStreamsInteractor getMutedStreamsInteractor,
      MuteInteractor muteInteractor, UnmuteInteractor unmuteInterator, StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.unwatchStreamInteractor = unwatchStreamInteractor;
        this.getMutedStreamsInteractor = getMutedStreamsInteractor;
        this.muteInteractor = muteInteractor;
        this.unmuteInterator = unmuteInterator;
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
        this.loadMutedStreamIds();
        this.loadFavorites();
    }

    private void loadMutedStreamIds() {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> mutedStreamsIds) {
                mutedStreamIds = mutedStreamsIds;
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO
            }
        });
    }

    protected void loadFavorites() {
        getFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                favoritesListView.hideLoading();
                if (streams.isEmpty()) {
                    favoritesListView.showEmpty();
                    favoritesListView.hideContent();
                } else {
                    List<StreamResultModel> streamModels = streamResultModelMapper.transform(streams);
                    favoritesListView.setMutedStreamIds(mutedStreamIds);
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

    public void onFavoriteLongClicked(final StreamResultModel stream) {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> mutedStreamIds) {
                if (mutedStreamIds.contains(stream.getStreamModel().getIdStream())) {
                    favoritesListView.showContextMenuWithUnmute(stream);
                } else {
                    favoritesListView.showContextMenuWithMute(stream);
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO quitar?
            }
        });
    }

    public void onMuteClicked(StreamResultModel stream) {
        muteInteractor.mute(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadMutedStreamIds();
                loadFavorites();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO
            }
        });
    }

    public void onUnmuteClicked(StreamResultModel stream) {
        unmuteInterator.unmute(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadMutedStreamIds();
                loadFavorites();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO
            }
        });
    }

    @Override
    public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.loadMutedStreamIds();
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
            @Override public void onCompleted() {
                loadFavorites();
            }
        });
    }

    @Subscribe
    @Override
    public void onUnwatchDone(UnwatchDone.Event event) {
        loadFavorites();
    }

    @Subscribe
    @Override
    public void onStreamMuted(StreamMuted.Event event) {
        this.loadMutedStreamIds();
        this.loadFavorites();
    }
}
