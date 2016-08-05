package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.StreamMuted;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import com.shootr.mobile.domain.repository.Remote;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class MuteInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final MuteRepository localMuteRepository;
  private final MuteRepository remoteMuteRepository;
  private final BusPublisher busPublisher;

  private String idStream;
  private CompletedCallback callback;

  @Inject public MuteInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local MuteRepository localMuteRepository,
      @Remote MuteRepository remoteMuteRepository, BusPublisher busPublisher) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localMuteRepository = localMuteRepository;
    this.remoteMuteRepository = remoteMuteRepository;
    this.busPublisher = busPublisher;
  }

  public void mute(String idStream, CompletedCallback callback) {
    this.idStream = checkNotNull(idStream);
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localMuteRepository.mute(idStream);
    notifyAdditionToBus();
    remoteMuteRepository.mute(idStream);
    notifyCompleted();
  }

  private void notifyCompleted() {
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