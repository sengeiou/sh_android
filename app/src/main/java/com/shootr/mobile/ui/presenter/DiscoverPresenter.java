package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.discover.GetLocalDiscoveredInteractor;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.discover.GetDiscoveredInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.DiscoveredModelMapper;
import com.shootr.mobile.ui.views.DiscoverView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class DiscoverPresenter implements Presenter {

  private final GetDiscoveredInteractor getDiscoveredInteractor;
  private final GetLocalDiscoveredInteractor getLocalDiscoveredInteractor;
  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  private final DiscoveredModelMapper discoveredModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private DiscoverView discoverView;
  private boolean hasBeenPaused;

  @Inject public DiscoverPresenter(GetDiscoveredInteractor getDiscoveredInteractor,
      GetLocalDiscoveredInteractor getLocalDiscoveredInteractor, AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      DiscoveredModelMapper discoveredModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.getDiscoveredInteractor = getDiscoveredInteractor;
    this.getLocalDiscoveredInteractor = getLocalDiscoveredInteractor;
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.discoveredModelMapper = discoveredModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(DiscoverView discoverView) {
    this.discoverView = discoverView;
    loadDiscover();
  }

  public void loadDiscover() {
    getDiscoveredInteractor.getDiscovered(new Interactor.Callback<List<Discovered>>() {
      @Override public void onLoaded(List<Discovered> discovereds) {
        discoverView.renderDiscover(discoveredModelMapper.transform(discovereds));
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

  public void streamClicked(StreamModel stream) {
    discoverView.navigateToStreamTimeline(stream.getIdStream(), stream.getTitle(),
        stream.getAuthorId());
  }

  public void addStreamToFavorites(final DiscoveredModel discoveredModel) {
    addToFavoritesInteractor.addToFavorites(discoveredModel.getStreamModel().getIdStream(),
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

  public void removeFromFavorites(final DiscoveredModel discoveredModel) {
    removeFromFavoritesInteractor.removeFromFavorites(discoveredModel.getStreamModel().getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            loadLocalDiscover();
          }
        });
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadLocalDiscover();
    }
  }

  @Override public void pause() {
    this.hasBeenPaused = true;
  }
}