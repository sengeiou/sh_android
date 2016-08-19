package com.shootr.mobile.domain.interactor.discover;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetLocalDiscoveredInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalDiscoveredRepository internalDiscoveredRepository;
  private final InternalFavoriteRepository localFavoriteRepository;

  private Callback<List<Discovered>> callback;

  @Inject public GetLocalDiscoveredInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      InternalDiscoveredRepository internalDiscoveredRepository,
      InternalFavoriteRepository localFavoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.internalDiscoveredRepository = internalDiscoveredRepository;
    this.localFavoriteRepository = localFavoriteRepository;
  }

  public void getDiscovered(Callback<List<Discovered>> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<Discovered> localeDiscovereds = internalDiscoveredRepository.getDiscovered();
    List<Favorite> favorites = localFavoriteRepository.getFavorites();
    mapDiscovereds(localeDiscovereds, favorites);
    notifyResult(localeDiscovereds);
  }

  private void mapDiscovereds(List<Discovered> discovereds, List<Favorite> favorites) {
    List<String> favoriteStreamIds = new ArrayList<>(favorites.size());
    for (Favorite favorite : favorites) {
      favoriteStreamIds.add(favorite.getIdStream());
    }
    for (Discovered discovered : discovereds) {
      discovered.setFaved(favoriteStreamIds.contains(discovered.getStream().getId()));
    }
  }

  private void notifyResult(final List<Discovered> discovereds) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(discovereds);
      }
    });
  }
}
