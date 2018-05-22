package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.shot.NewShotDetail;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import javax.inject.Inject;

public class GetCachedShotDetailInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;

  private String idShot;
  private Callback<NewShotDetail> callback;

  @Inject public GetCachedShotDetailInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository internalShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = internalShotRepository;
  }

  public void getTimeline(String idShot, Callback<NewShotDetail> callback) {
    this.idShot = idShot;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notify(localShotRepository.getCachedShotDetail(idShot));
  }

  private void notify(final NewShotDetail response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }
}
