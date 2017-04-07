package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.GetDiscoverTimelineInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.DiscoverTimelineModelMapper;
import com.shootr.mobile.ui.views.DiscoverTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class DiscoverTimelinePresenter implements Presenter {

  private final GetDiscoverTimelineInteractor getDiscoverTimelineInteractor;
  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final ShareShotInteractor shareShotInteractor;
  private final DiscoverTimelineModelMapper discoverTimelineModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private DiscoverTimelineView discoverView;
  private boolean hasBeenPaused;

  @Inject public DiscoverTimelinePresenter(GetDiscoverTimelineInteractor getDiscoverTimelineInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ShareShotInteractor shareShotInteractor,
      DiscoverTimelineModelMapper discoverTimelineModelMapper,
      ErrorMessageFactory errorMessageFactory) {
    this.getDiscoverTimelineInteractor = getDiscoverTimelineInteractor;
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.shareShotInteractor = shareShotInteractor;
    this.discoverTimelineModelMapper = discoverTimelineModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(DiscoverTimelineView discoverView) {
    this.discoverView = discoverView;
    loadDiscover(false);
  }

  private void loadDiscover(boolean onlyFromLocal) {
    getDiscoverTimelineInteractor.getDiscovered(onlyFromLocal, new Interactor.Callback<DiscoverTimeline>() {
      @Override public void onLoaded(DiscoverTimeline discoverTimeline) {
        if (discoverTimeline.getDiscoverStreams().isEmpty()) {
          discoverView.showEmpty();
        } else {
          discoverView.renderDiscover(discoverTimelineModelMapper.map(discoverTimeline));
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        discoverView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void streamClicked(String streamId) {
    discoverView.navigateToStreamTimeline(streamId);
  }

  public void shotClicked(ShotModel shotModel) {
    discoverView.navigateToShotDetail(shotModel);
  }

  public void onAvatarClicked(String userId) {
    discoverView.navigateToUserProfile(userId);
  }

  public void addStreamToFavorites(final StreamModel streamModel) {
    addToFavoritesInteractor.addToFavorites(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            discoverView.renderNewFavorite(streamModel);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void removeFromFavorites(final StreamModel streamModel) {
    removeFromFavoritesInteractor.removeFromFavorites(
        streamModel.getIdStream(), new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            discoverView.removeFavorite(streamModel);
          }
        });
  }

  public void markNiceShot(final ShotModel shotModel) {
    markNiceShotInteractor.markNiceShot(shotModel.getIdShot(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            discoverView.renderNiceMarked(shotModel);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void unmarkNiceShot(final String idShot) {
    unmarkNiceShotInteractor.unmarkNiceShot(idShot,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            discoverView.renderNiceUnmarked(idShot);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadDiscover(true);
    }
  }

  @Override public void pause() {
    this.hasBeenPaused = true;
  }

  public void reshoot(ShotModel shotModel) {
    shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        discoverView.showReshot();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        discoverView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

}
