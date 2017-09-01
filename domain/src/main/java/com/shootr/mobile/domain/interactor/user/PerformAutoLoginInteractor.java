package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.nice.NicerRepository;
import javax.inject.Inject;

public class PerformAutoLoginInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final NicerRepository nicerRepository;
  private final ExternalFavoriteRepository favoriteRepository;
  private String idUser;

  @Inject public PerformAutoLoginInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, NicerRepository nicerRepository,
      ExternalFavoriteRepository favoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.nicerRepository = nicerRepository;
    this.favoriteRepository = favoriteRepository;
  }

  public void storePostAutoLoginInfo(String idUser) {
    this.idUser = idUser;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      storeFavoritesStreams(idUser);
    } catch (ShootrException unknownException) {
      /* no-op */
    }
  }

  private void storeFavoritesStreams(String idUser) {
    favoriteRepository.getFavorites(idUser);
  }
}
