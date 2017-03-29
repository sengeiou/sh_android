package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetNewFilteredShotsInteractor implements Interactor {

  private final InternalShotRepository internalShotRepository;
  private final StreamRepository localStreamRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private String idStream;
  private Callback<Boolean> callback;

  @Inject public GetNewFilteredShotsInteractor(InternalShotRepository internalShotRepository,
      @Local StreamRepository localStreamRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
    this.internalShotRepository = internalShotRepository;
    this.localStreamRepository = localStreamRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void hasNewFilteredShots(String idStream, Callback<Boolean> callback) {
    this.idStream = idStream;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    String lastFiltered = localStreamRepository.getLastTimeFiltered(idStream);
    notifyLoaded(internalShotRepository.hasNewFilteredShots(idStream, lastFiltered));
  }

  protected void notifyLoaded(final Boolean hasNewShots) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(hasNewShots);
      }
    });
  }

}
