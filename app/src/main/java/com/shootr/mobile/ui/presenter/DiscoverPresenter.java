package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.ChannelsBadgeChanged;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.GetDiscoveredInteractor;
import com.shootr.mobile.domain.interactor.discover.GetLocalDiscoveredInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.DiscoveredModelMapper;
import com.shootr.mobile.ui.views.DiscoverView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class DiscoverPresenter implements Presenter, ChannelsBadgeChanged.Receiver {

  private final GetDiscoveredInteractor getDiscoveredInteractor;
  private final GetLocalDiscoveredInteractor getLocalDiscoveredInteractor;
  private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final Bus bus;
  private final DiscoveredModelMapper discoveredModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private DiscoverView discoverView;
  private boolean hasBeenPaused;

  @Inject public DiscoverPresenter(GetDiscoveredInteractor getDiscoveredInteractor,
      GetLocalDiscoveredInteractor getLocalDiscoveredInteractor,
      GetFavoriteStreamsInteractor getFavoriteStreamsInteractor, AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, @Main Bus bus,
      DiscoveredModelMapper discoveredModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getDiscoveredInteractor = getDiscoveredInteractor;
    this.getLocalDiscoveredInteractor = getLocalDiscoveredInteractor;
    this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.bus = bus;
    this.discoveredModelMapper = discoveredModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(DiscoverView discoverView) {
    this.discoverView = discoverView;
    loadDiscover();
  }
  private void loadFavorites() {
    discoverView.showLoading();
    getFavoriteStreamsInteractor.loadFavoriteStreamsFromLocalOnly(new Interactor.Callback<List<StreamSearchResult>>() {
      @Override public void onLoaded(List<StreamSearchResult> streamSearchResults) {
        discoverView.hideLoading();
        if (streamSearchResults.size() < 3) {
          discoverView.showBanner();
        } else {
          discoverView.hideBanner();
        }
      }
    });
  }

  public void loadDiscover() {
    getDiscoveredInteractor.getDiscovered(new Interactor.Callback<List<Discovered>>() {
      @Override public void onLoaded(List<Discovered> discovereds) {
        discoverView.renderDiscover(discoveredModelMapper.transform(discovereds));
        loadFavorites();
        if (discovereds.size() == 0) {
          discoverView.showEmpty();
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        discoverView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void loadLocalDiscover() {
    getLocalDiscoveredInteractor.getDiscovered(new Interactor.Callback<List<Discovered>>() {
      @Override public void onLoaded(List<Discovered> discovereds) {
        discoverView.renderDiscover(discoveredModelMapper.transform(discovereds));
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

  public void addStreamToFavorites(final String idStream) {
    addToFavoritesInteractor.addToFavorites(idStream,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLocalDiscover();
            loadFavorites();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
            loadLocalDiscover();
          }
        });
  }

  public void removeFromFavorites(String idStream) {
    removeFromFavoritesInteractor.removeFromFavorites(
        idStream, new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLocalDiscover();
            loadFavorites();
          }
        });
  }

  public void markNiceShot(DiscoveredModel discoveredModel) {
    markNiceShotInteractor.markNiceShot(discoveredModel.getShotModel().getIdShot(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLocalDiscover();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
            loadLocalDiscover();
          }
        });
  }

  public void unmarkNiceShot(DiscoveredModel discoveredModel) {
    unmarkNiceShotInteractor.unmarkNiceShot(discoveredModel.getShotModel().getIdShot(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLocalDiscover();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            discoverView.showError(errorMessageFactory.getMessageForError(error));
            loadLocalDiscover();
          }
        });
  }

  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      loadLocalDiscover();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    this.hasBeenPaused = true;
  }

  @Subscribe @Override public void onBadgeChanged(ChannelsBadgeChanged.Event event) {
    if (event.getUnreadFollowChannels() > 0) {
      discoverView.updateChannelBadge(event.getUnreadFollowChannels(), true);
    } else {
      discoverView.updateChannelBadge(event.getUnreadChannels(), false);
    }
  }
}
