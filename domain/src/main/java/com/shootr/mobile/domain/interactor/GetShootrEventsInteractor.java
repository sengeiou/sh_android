package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import javax.inject.Inject;

public class GetShootrEventsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrEventRepository remoteShootrEventRepository;

  @Inject public GetShootrEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote ShootrEventRepository remoteShootrEventRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShootrEventRepository = remoteShootrEventRepository;
  }

  public void synchronizeShootrEvents() {
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remoteShootrEventRepository.getShootrEvents();
    } catch (ServerCommunicationException networkError) {
            /* swallow silently */
    }
  }


}
