package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class UnmuteInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final BusPublisher busPublisher;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;

  private String idStream;
  private CompletedCallback callback;

  @Inject public UnmuteInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, BusPublisher busPublisher,
      ExternalStreamRepository remoteStreamRepository, @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.busPublisher = busPublisher;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void unmute(String idStream, CompletedCallback callback) {
    this.idStream = checkNotNull(idStream);
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localStreamRepository.unmute(idStream);
    notifyAdditionToBus();
    remoteStreamRepository.unmute(idStream);
    notifyCompleted();
  }

  private void notifyCompleted() {
    notifyAdditionToBus();
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }

  protected void notifyAdditionToBus() {
    busPublisher.post(new StreamMuted.Event());
  }
}
