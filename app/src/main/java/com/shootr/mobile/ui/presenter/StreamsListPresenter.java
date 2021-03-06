package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.GetLandingStreamsInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.FollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.HideStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnfollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.PromotedLandingItem;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.ImageMediaModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamsListPresenter implements Presenter, UnwatchDone.Receiver, StreamMuted.Receiver {

  private final GetLandingStreamsInteractor getLandingStreamsInteractor;
  private final FollowStreamInteractor followStreamInteractor;
  private final UnfollowStreamInteractor unfollowStreamInteractor;
  private final UnwatchStreamInteractor unwatchStreamInteractor;
  private final ShareStreamInteractor shareStreamInteractor;
  private final MuteInteractor muteInteractor;
  private final HideStreamInteractor hideStreamInteractor;
  private final UnmuteInteractor unmuteInterator;
  private final StreamResultModelMapper streamResultModelMapper;
  private final ImageMediaModelMapper imageMediaModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final SessionRepository sessionRepository;
  private final ErrorMessageFactory errorMessageFactory;
  private final Bus bus;

  private StreamsListView streamsListView;
  private boolean hasBeenPaused;
  ArrayList<PromotedLandingItemModel> promotedItems;

  @Inject public StreamsListPresenter(GetLandingStreamsInteractor getLandingStreamsInteractor,
      FollowStreamInteractor followStreamInteractor,
      UnfollowStreamInteractor unfollowStreamInteractor,
      UnwatchStreamInteractor unwatchStreamInteractor, ShareStreamInteractor shareStreamInteractor,
      MuteInteractor muteInteractor, HideStreamInteractor hideStreamInteractor,
      UnmuteInteractor unmuteInterator, StreamResultModelMapper streamResultModelMapper,
      ImageMediaModelMapper imageMediaModelMapper, StreamModelMapper streamModelMapper,
      SessionRepository sessionRepository, ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
    this.getLandingStreamsInteractor = getLandingStreamsInteractor;
    this.followStreamInteractor = followStreamInteractor;
    this.unfollowStreamInteractor = unfollowStreamInteractor;
    this.unwatchStreamInteractor = unwatchStreamInteractor;
    this.shareStreamInteractor = shareStreamInteractor;
    this.muteInteractor = muteInteractor;
    this.hideStreamInteractor = hideStreamInteractor;
    this.unmuteInterator = unmuteInterator;
    this.streamResultModelMapper = streamResultModelMapper;
    this.imageMediaModelMapper = imageMediaModelMapper;
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
    this.setView(streamsListView);
    loadLandingStreams();
  }

  public void refresh() {
    loadLandingStreams();
  }

  public void selectStream(StreamModel stream) {
    selectStream(stream.getIdStream(), stream.getTitle());
  }

  private void selectStream(final String idStream, String streamTag) {
    streamsListView.navigateToStreamTimeline(idStream, streamTag);
  }

  public void loadLandingStreams() {
    streamsListView.showLoading();
    getLandingStreamsInteractor.getLandingStreams(new Interactor.Callback<LandingStreams>() {
      @Override public void onLoaded(LandingStreams landingStreams) {
        if (landingStreams != null) {
          streamsListView.hideLoading();
          streamsListView.renderLanding(streamModelMapper.transformLandingStreams(landingStreams));
          setupPromoteds(landingStreams);
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showViewError(error);
      }
    });
  }

  private void setupPromoteds(LandingStreams landingStreams) {
    promotedItems = new ArrayList<>();
    for (PromotedLandingItem promotedItem : landingStreams.getPromoted().getPromotedItems()) {
      if (promotedItem.getData().getResultType().equals(PrintableType.STREAM)) {
        PromotedLandingItemModel promotedModel = new PromotedLandingItemModel();
        promotedModel.setData(streamModelMapper.transform((Stream) promotedItem.getData()));
        promotedModel.setImageMediaModel(imageMediaModelMapper.transform(promotedItem.getImage()));
        promotedModel.setSubtitle(promotedItem.getSubtitle());

        promotedItems.add(promotedModel);
      }
    }

    streamsListView.renderPromoteds(promotedItems);
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

  public void addToFavorites(final StreamModel streamResultModel, final Boolean notify) {
    followStreamInteractor.follow(streamResultModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            if (notify) {
              streamResultModel.setFollowing(true);
              streamsListView.renderFollow(streamResultModel);
              streamsListView.showAddedToFavorites();
              renderFollowInPromoted(streamResultModel, true);
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            showViewError(error);
          }
        });
  }

  private void renderFollowInPromoted(StreamModel streamResultModel, boolean isFollowing) {
    for (PromotedLandingItemModel promotedItem : promotedItems) {
      if (promotedItem.getData() instanceof StreamModel
          && ((StreamModel) promotedItem.getData()).getIdStream()
          .equals(streamResultModel.getIdStream())) {
        streamResultModel.setFollowing(isFollowing);
        promotedItem.setData(streamResultModel);
      }
    }

    streamsListView.renderPromoteds(promotedItems);
  }

  public void removeFromFavorites(final StreamModel streamResultModel, final Boolean notify) {
    unfollowStreamInteractor.unfollow(streamResultModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            if (notify) {
              streamResultModel.setFollowing(false);
              streamsListView.renderFollow(streamResultModel);
              streamsListView.showRemovedFromFavorites();
              renderFollowInPromoted(streamResultModel, false);
            }
          }
        });
  }

  public void shareStream(StreamModel stream) {
    shareStreamInteractor.shareStream(stream.getIdStream(), new Interactor.CompletedCallback() {
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
    /* no-op */
  }

  public void onStreamLongClicked(final StreamModel stream) {
    if (stream.isMuted()) {
      streamsListView.showContextMenuWithUnmute(stream);
    } else {
      streamsListView.showContextMenuWithMute(stream);
    }
  }

  public void onMuteClicked(final StreamModel stream) {
    muteInteractor.mute(stream.getIdStream(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        stream.setMuted(true);
        streamsListView.renderMute(stream);
      }
    });
  }

  public void onHideStreamClicked(final StreamModel stream) {
    hideStreamInteractor.hideStream(stream.getIdStream());
  }

  public void onUnmuteClicked(final StreamModel stream) {
    unmuteInterator.unmute(stream.getIdStream(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        stream.setMuted(false);
        streamsListView.renderMute(stream);
      }
    });
  }

  @Subscribe @Override public void onStreamMuted(StreamMuted.Event event) {
    /* no-op */
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
  //endregion
}
