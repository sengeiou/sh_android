package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.FollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnfollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class StreamTimelineOptionsPresenter implements Presenter {

  private final FollowStreamInteractor followStreamInteractor;
  private final UnfollowStreamInteractor unfollowStreamInteractor;
  private final MuteInteractor muteInteractor;
  private final UnmuteInteractor unmuteInteractor;
  private final GetStreamInteractor getStreamInteractor;

  private final ErrorMessageFactory errorMessageFactory;

  private StreamTimelineOptionsView streamTimelineOptionsView;
  private String idStream;
  private boolean hasBeenPaused;

  @Inject
  public StreamTimelineOptionsPresenter(FollowStreamInteractor followStreamInteractor,
      UnfollowStreamInteractor unfollowStreamInteractor, MuteInteractor muteInteractor,
      UnmuteInteractor unmuteInteractor, GetStreamInteractor getStreamInteractor,
      ErrorMessageFactory errorMessageFactory) {
    this.followStreamInteractor = followStreamInteractor;
    this.unfollowStreamInteractor = unfollowStreamInteractor;
    this.muteInteractor = muteInteractor;
    this.unmuteInteractor = unmuteInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void setView(StreamTimelineOptionsView streamTimelineOptionsView) {
    this.streamTimelineOptionsView = streamTimelineOptionsView;
  }

  public void initialize(StreamTimelineOptionsView streamTimelineOptionsView, String idStream) {
    this.idStream = idStream;
    this.setView(streamTimelineOptionsView);
    this.loadMuteAndFollowStatus();
  }

  private void loadMuteAndFollowStatus() {
    getStreamInteractor.loadStream(idStream, new GetStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        if (stream.isMuted()) {
          streamTimelineOptionsView.showUnmuteButton();
          streamTimelineOptionsView.hideMuteButton();
        } else {
          streamTimelineOptionsView.showMuteButton();
          streamTimelineOptionsView.hideUnmuteButton();
        }
        if (!stream.isFollowing()) {
          streamTimelineOptionsView.showAddToFavoritesButton();
          streamTimelineOptionsView.hideRemoveFromFavoritesButton();
        } else {
          streamTimelineOptionsView.showRemoveFromFavoritesButton();
          streamTimelineOptionsView.hideAddToFavoritesButton();
        }
      }
    });
  }

  public void addToFavorites() {
    followStreamInteractor.follow(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineOptionsView.hideAddToFavoritesButton();
        streamTimelineOptionsView.showRemoveFromFavoritesButton();
        streamTimelineOptionsView.showAddedToFavorites();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showErrorInView(error);
      }
    });
  }

  private void showErrorInView(ShootrException error) {
    streamTimelineOptionsView.showError(errorMessageFactory.getMessageForError(error));
  }

  public void removeFromFavorites() {
    unfollowStreamInteractor.unfollow(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineOptionsView.showAddToFavoritesButton();
        streamTimelineOptionsView.hideRemoveFromFavoritesButton();
      }
    });
  }

  public void mute() {
    muteInteractor.mute(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineOptionsView.hideMuteButton();
        streamTimelineOptionsView.showUnmuteButton();
      }
    });
  }

  public void unmute() {
    unmuteInteractor.unmute(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        streamTimelineOptionsView.hideUnmuteButton();
        streamTimelineOptionsView.showMuteButton();
      }
    });
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadMuteAndFollowStatus();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }
}
