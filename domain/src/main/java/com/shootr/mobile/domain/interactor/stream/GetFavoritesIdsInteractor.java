package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetFavoritesIdsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalFavoriteRepository localFavoriteRepository;

  private Callback<List<String>> callback;

  @Inject public GetFavoritesIdsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalFavoriteRepository localFavoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localFavoriteRepository = localFavoriteRepository;
  }

  public void loadFavoriteStreams(Callback<List<String>> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalFavorites();
  }

  private void loadLocalFavorites() {
    List<Favorite> favorites = localFavoriteRepository.getFavorites();
    mapFavorites(favorites);
  }

  private void mapFavorites(List<Favorite> favorites) {
    ArrayList<String> favoritesIds = new ArrayList<>();
    for (Favorite favorite : favorites) {
      favoritesIds.add(favorite.getIdStream());
    }
    notifyLoaded(favoritesIds);
  }

  private void notifyLoaded(final List<String> favorites) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(favorites);
      }
    });
  }
}
