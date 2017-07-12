package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import javax.inject.Inject;

public class GetShootrEventsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ShootrEventRepository remoteShootrEventRepository;
  private final RecentSearchRepository remoteRecentSearchRepository;

  @Inject public GetShootrEventsInteractor(InteractorHandler interactorHandler,
      @Remote ShootrEventRepository remoteShootrEventRepository,
      RecentSearchRepository remoteRecentSearchRepository) {
    this.interactorHandler = interactorHandler;
    this.remoteShootrEventRepository = remoteShootrEventRepository;
    this.remoteRecentSearchRepository = remoteRecentSearchRepository;
  }

  public void synchronizeShootrEvents() {
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      if(remoteRecentSearchRepository.isRecentSearchEmpty()) {
        remoteShootrEventRepository.getShootrEvents();
      }
    } catch (ServerCommunicationException networkError) {
            /* swallow silently */
    }
  }


}
