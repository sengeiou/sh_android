package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.QueueElementRepository;
import javax.inject.Inject;

public class SendCacheQueueInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final QueueElementRepository remoteElementRepository;

  @Inject public SendCacheQueueInteractor(InteractorHandler interactorHandler,
      QueueElementRepository remoteElementRepository) {
    this.interactorHandler = interactorHandler;
    this.remoteElementRepository = remoteElementRepository;
  }

  public void sendCacheQueue() {
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
    remoteElementRepository.sendQueue();
  }
}