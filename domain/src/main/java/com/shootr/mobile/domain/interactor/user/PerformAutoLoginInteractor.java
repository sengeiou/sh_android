package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Nicer;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import com.shootr.mobile.domain.repository.nice.NicerRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PerformAutoLoginInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final NicerRepository nicerRepository;
  private final InternalNiceShotRepository localNiceShotRepository;
  private final ExternalFavoriteRepository favoriteRepository;
  private String idUser;

  @Inject public PerformAutoLoginInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, NicerRepository nicerRepository,
      InternalNiceShotRepository localNiceShotRepository,
      ExternalFavoriteRepository favoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.nicerRepository = nicerRepository;
    this.localNiceShotRepository = localNiceShotRepository;
    this.favoriteRepository = favoriteRepository;
  }

  public void storePostAutoLoginInfo(String idUser) {
    this.idUser = idUser;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      storeFavoritesStreams(idUser);
      storeNicedShots(idUser);
    } catch (ShootrException unknownException) {
      /* no-op */
    }
  }

  private void storeNicedShots(String idUser) {
    List<Nicer> nices = nicerRepository.getNices(idUser);
    List<String> nicedIdShots = new ArrayList<>(nices.size());
    for (Nicer nice : nices) {
      nicedIdShots.add(nice.getIdShot());
    }
    localNiceShotRepository.markAll(nicedIdShots);
  }

  private void storeFavoritesStreams(String idUser) {
    favoriteRepository.getFavorites(idUser);
  }
}
