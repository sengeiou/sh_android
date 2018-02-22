package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.log.LogRepository;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import javax.inject.Inject;

public class SendShootrEventStatsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ShootrEventRepository remoteEventRepository;
  private final LogRepository remoteLogRepository;

  @Inject public SendShootrEventStatsInteractor(InteractorHandler interactorHandler,
      @Remote ShootrEventRepository remoteEventRepository,
      @Remote LogRepository remoteLogRepository) {
    this.interactorHandler = interactorHandler;
    this.remoteEventRepository = remoteEventRepository;
    this.remoteLogRepository = remoteLogRepository;
  }

  public void sendShootrEvents() {
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      send();
    } catch (ServerCommunicationException error) {
      /* no-op */
    } catch (Exception gralError) {
      /* no-op */
    }
  }

  private void send() {
    remoteEventRepository.sendShotEvents();
    remoteLogRepository.sendLogs();
  }
}