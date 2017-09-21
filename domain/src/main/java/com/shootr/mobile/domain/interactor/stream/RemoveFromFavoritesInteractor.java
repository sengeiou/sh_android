package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import javax.inject.Inject;

public class RemoveFromFavoritesInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalFavoriteRepository localFavoriteRepository;
  private final ExternalFavoriteRepository remoteFavoriteRepository;

  private CompletedCallback callback;

  private String idStream;

  @Inject public RemoveFromFavoritesInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalFavoriteRepository localFavoriteRepository,
      ExternalFavoriteRepository remoteFavoriteRepository) {
    this.localFavoriteRepository = localFavoriteRepository;
    this.interactorHandler = interactorHandler;
    this.remoteFavoriteRepository = remoteFavoriteRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void removeFromFavorites(String idStream, CompletedCallback callback) {
    this.callback = callback;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Favorite existingFavorite = localFavoriteRepository.getFavoriteByStream(idStream);
    if (existingFavorite != null) {
      localFavoriteRepository.removeFavoriteByStream(existingFavorite.getIdStream());
      remoteFavoriteRepository.removeFavoriteByStream(existingFavorite.getIdStream());
    } else {
      remoteFavoriteRepository.removeFavoriteByStream(idStream);
    }
    notifyCompleted();
  }

  protected void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }
}
