package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotEventRepository;
import javax.inject.Inject;

public class SendShotEventStatsIneteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ShotEventRepository remoteEventRepository;

  @Inject public SendShotEventStatsIneteractor(InteractorHandler interactorHandler,
      @Remote ShotEventRepository remoteEventRepository) {
    this.interactorHandler = interactorHandler;
    this.remoteEventRepository = remoteEventRepository;
  }

  public void sendShotsStats() {
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      sendshotEvents();
    } catch (ServerCommunicationException error) {
      /* no-op */
    }
  }

  private void sendshotEvents() {
    remoteEventRepository.sendShotEvents();
  }
}