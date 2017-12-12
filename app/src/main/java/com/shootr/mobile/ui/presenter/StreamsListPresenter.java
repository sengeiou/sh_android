package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.GetLandingStreamsInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.FollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.interactor.stream.UnfollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class StreamsListPresenter implements Presenter, UnwatchDone.Receiver, StreamMuted.Receiver {

  private final StreamsListInteractor streamsListInteractor;
  private final GetLandingStreamsInteractor getLandingStreamsInteractor;
  private final FollowStreamInteractor followStreamInteractor;
  private final UnfollowStreamInteractor unfollowStreamInteractor;
  private final UnwatchStreamInteractor unwatchStreamInteractor;
  private final ShareStreamInteractor shareStreamInteractor;
  private final MuteInteractor muteInteractor;
  private final UnmuteInteractor unmuteInterator;
  private final StreamResultModelMapper streamResultModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final SessionRepository sessionRepository;
  private final ErrorMessageFactory errorMessageFactory;
  private final Bus bus;

  private StreamsListView streamsListView;
  private boolean hasBeenPaused;

  @Inject public StreamsListPresenter(StreamsListInteractor streamsListInteractor,
      GetLandingStreamsInteractor getLandingStreamsInteractor, FollowStreamInteractor followStreamInteractor,
      UnfollowStreamInteractor unfollowStreamInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor, ShareStreamInteractor shareStreamInteractor,
      MuteInteractor muteInteractor, UnmuteInteractor unmuteInterator,
      StreamResultModelMapper streamResultModelMapper, StreamModelMapper streamModelMapper,
      SessionRepository sessionRepository, ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
    this.streamsListInteractor = streamsListInteractor;
    this.getLandingStreamsInteractor = getLandingStreamsInteractor;
    this.followStreamInteractor = followStreamInteractor;
    this.unfollowStreamInteractor = unfollowStreamInteractor;
    this.unwatchStreamInteractor = unwatchStreamInteractor;
    this.shareStreamInteractor = shareStreamInteractor;
    this.muteInteractor = muteInteractor;
    this.unmuteInterator = unmuteInterator;
    this.streamResultModelMapper = streamResultModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.sessionRepository = sessionRepository;
    this.errorMessageFactory = errorMessageFactory;
    this.bus = bus;
  }
  //endregion

  public void setView(StreamsListView streamsListView) {
    this.streamsListView = streamsListView;
  }

  public void initialize(StreamsListView streamsListView) {
    loadLandingStreams();
    this.setView(streamsListView);
  }

  public void refresh() {
    loadLandingStreams();
  }

  public void selectStream(StreamModel stream) {
    selectStream(stream.getIdStream(), stream.getTitle(),
        stream.getAuthorId());
  }

  private void selectStream(final String idStream, String streamTag, String authorId) {
    streamsListView.navigateToStreamTimeline(idStream, streamTag, authorId);
  }

  public void unwatchStream() {
    unwatchStreamInteractor.unwatchStream(new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        loadLandingStreams();
        removeCurrentWatchingStream();
      }
    });
  }

  private void removeCurrentWatchingStream() {
    streamsListView.setCurrentWatchingStreamId(null);
  }

  private void loadLandingStreams() {
    getLandingStreamsInteractor.getLandingStreams(new Interactor.Callback<LandingStreams>() {
      @Override public void onLoaded(LandingStreams landingStreams) {
        streamsListView.hideLoading();
        streamsListView.renderLanding(streamModelMapper.transformLandingStreams(landingStreams));
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
      List<StreamResultModel> streamResultModels =
          streamResultModelMapper.transformWithFavorites(streamSearchResults);
      this.renderViewStreamsList(streamResultModels);
      StreamSearchResult currentWatchingStream = resultList.getCurrentWatchingStream();
      this.setViewCurrentVisibleWatchingStream(
          streamResultModelMapper.transform(currentWatchingStream));
    } else {
      streamsListView.showLoading();
    }
  }

  public void streamCreated(String streamId) {
    streamsListView.navigateToCreatedStreamDetail(streamId);
  }

  private void setViewCurrentVisibleWatchingStream(StreamResultModel currentVisibleStream) {
    if (currentVisibleStream != null) {
      streamsListView.setCurrentWatchingStreamId(currentVisibleStream);
    }
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

  public void addToFavorites(StreamModel streamResultModel, final Boolean notify) {
    followStreamInteractor.follow(streamResultModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            if (notify) {
              streamsListView.showAddedToFavorites();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  public void removeFromFavorites(StreamModel streamResultModel, final Boolean notify) {
    unfollowStreamInteractor.unfollow(streamResultModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            if (notify) {
              streamsListView.showRemovedFromFavorites();
            }
          }
        });
  }

  public void shareStream(StreamModel stream) {
    shareStreamInteractor.shareStream(stream.getIdStream(),
        new Interactor.CompletedCallback() {
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
    this.loadLandingStreams();
  }

  public void onStreamLongClicked(final StreamModel stream) {
    if (stream.isMuted()) {
      streamsListView.showContextMenuWithUnmute(stream);
    } else {
      streamsListView.showContextMenuWithMute(stream);
    }
  }

  public void onMuteClicked(StreamModel stream) {
    muteInteractor.mute(stream.getIdStream(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        loadLandingStreams();
      }
    });
  }

  public void onUnmuteClicked(StreamModel stream) {
    unmuteInterator.unmute(stream.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLandingStreams();
          }
        });
  }

  @Subscribe @Override public void onStreamMuted(StreamMuted.Event event) {
    loadLandingStreams();
  }

  //region Lifecycle
  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      loadLandingStreams();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
  }

  public void clickMyStreams() {
    streamsListView.navigateToMyStreams(sessionRepository.getCurrentUserId(), true);
  }
  //endregion
}
