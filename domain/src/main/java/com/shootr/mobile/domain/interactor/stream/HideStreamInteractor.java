package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class HideStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final BusPublisher busPublisher;
  private final ExternalStreamRepository remoteStreamRepository;

  private String idStream;

  @Inject public HideStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, BusPublisher busPublisher,
      ExternalStreamRepository remoteStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.busPublisher = busPublisher;
    this.remoteStreamRepository = remoteStreamRepository;
  }

  public void hideStream(String idStream) {
    this.idStream = checkNotNull(idStream);
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    remoteStreamRepository.hide(idStream);
  }

}