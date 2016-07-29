package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class StreamsListPresenter implements Presenter, UnwatchDone.Receiver, StreamMuted.Receiver {

    private final StreamsListInteractor streamsListInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final UnwatchStreamInteractor unwatchStreamInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final GetMutedStreamsInteractor getMutedStreamsInteractor;
    private final MuteInteractor muteInteractor;
    private final UnmuteInteractor unmuteInterator;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private StreamsListView streamsListView;
    private boolean hasBeenPaused;
    private List<String> mutedStreamIds;

    @Inject public StreamsListPresenter(StreamsListInteractor streamsListInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, UnwatchStreamInteractor unwatchStreamInteractor,
      ShareStreamInteractor shareStreamInteractor, GetMutedStreamsInteractor getMutedStreamsInteractor,
      MuteInteractor muteInteractor, UnmuteInteractor unmuteInterator, StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
        this.streamsListInteractor = streamsListInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.unwatchStreamInteractor = unwatchStreamInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.getMutedStreamsInteractor = getMutedStreamsInteractor;
        this.muteInteractor = muteInteractor;
        this.unmuteInterator = unmuteInterator;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.bus = bus;
    }
    //endregion

    public void setView(StreamsListView streamsListView) {
        this.streamsListView = streamsListView;
    }

    public void initialize(StreamsListView streamsListView) {
        this.setView(streamsListView);
        this.loadMutedStreamIds();
        this.loadDefaultStreamList();
    }

    private void loadMutedStreamIds() {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> mutedStreamsIds) {
                mutedStreamIds = mutedStreamsIds;
            }
        });
    }

    public void refresh() {
        this.loadDefaultStreamList();
    }

    public void selectStream(StreamResultModel stream) {
        streamsListView.setCurrentWatchingStreamId(stream);
        selectStream(stream.getStreamModel().getIdStream(),
          stream.getStreamModel().getTitle(),
          stream.getStreamModel().getAuthorId());
    }

    private void selectStream(final String idStream, String streamTag, String authorId) {
        streamsListView.navigateToStreamTimeline(idStream, streamTag, authorId);
    }

    public void unwatchStream() {
        unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadDefaultStreamList();
                removeCurrentWatchingStream();
            }
        });
    }

    private void removeCurrentWatchingStream() {
        streamsListView.setCurrentWatchingStreamId(null);
    }

    protected void loadDefaultStreamList() {
        streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
            @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
                streamsListView.hideLoading();
                onDefaultStreamListLoaded(streamSearchResultList);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void onDefaultStreamListLoaded(StreamSearchResultList resultList) {
        List<StreamSearchResult> streamSearchResults = resultList.getStreamSearchResults();
        if (!streamSearchResults.isEmpty()) {
            List<StreamResultModel> streamResultModels = streamResultModelMapper.transform(streamSearchResults);
            streamsListView.setMutedStreamIds(mutedStreamIds);
            this.renderViewStreamsList(streamResultModels);
            StreamSearchResult currentWatchingStream = resultList.getCurrentWatchingStream();
            this.setViewCurrentVisibleWatchingStream(streamResultModelMapper.transform(currentWatchingStream));
        } else {
            streamsListView.showLoading();
        }
    }

    public void streamCreated(String streamId) {
        streamsListView.navigateToCreatedStreamDetail(streamId);
    }

    private void setViewCurrentVisibleWatchingStream(StreamResultModel currentVisibleStream) {
        streamsListView.setCurrentWatchingStreamId(currentVisibleStream);
    }

    private void renderViewStreamsList(List<StreamResultModel> streamModels) {
        streamsListView.showContent();
        streamsListView.hideEmpty();
        streamsListView.renderStream(streamModels);
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ShootrValidationException) {
            String errorCode = ((ShootrValidationException) error).getErrorCode();
            errorMessage = errorMessageFactory.getMessageForCode(errorCode);
        } else {
            errorMessage = errorMessageFactory.getMessageForError(error);
        }
        streamsListView.showError(errorMessage);
    }

    public void addToFavorites(StreamResultModel streamResultModel) {
        addToFavoritesInteractor.addToFavorites(streamResultModel.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  streamsListView.showAddedToFavorites();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showViewError(error);
              }
          });
    }

    public void shareStream(StreamResultModel stream) {
        shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                streamsListView.showStreamShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    @Subscribe @Override public void onUnwatchDone(UnwatchDone.Event event) {
        this.loadDefaultStreamList();
    }

    public void onStreamLongClicked(final StreamResultModel stream) {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> mutedStreamIds) {
                if (mutedStreamIds.contains(stream.getStreamModel().getIdStream())) {
                    streamsListView.showContextMenuWithUnmute(stream);
                } else {
                    streamsListView.showContextMenuWithMute(stream);
                }
            }
        });
    }

    public void onMuteClicked(StreamResultModel stream) {
        muteInteractor.mute(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadMutedStreamIds();
                loadDefaultStreamList();
            }
        });
    }

    public void onUnmuteClicked(StreamResultModel stream) {
        unmuteInterator.unmute(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadMutedStreamIds();
                loadDefaultStreamList();
            }
        });
    }

    @Subscribe @Override public void onStreamMuted(StreamMuted.Event event) {
        loadMutedStreamIds();
        loadDefaultStreamList();
    }

    //region Lifecycle
    @Override public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.loadMutedStreamIds();
            this.loadDefaultStreamList();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }
    //endregion
}
