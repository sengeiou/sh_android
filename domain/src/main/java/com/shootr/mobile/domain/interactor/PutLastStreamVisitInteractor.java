package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import javax.inject.Inject;

public class PutLastStreamVisitInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final TimeUtils timeUtils;
  private CompletedCallback completedCallback;
  private String idStream;

  @Inject public PutLastStreamVisitInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      TimeUtils timeUtils) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamRepository = localStreamRepository;
    this.timeUtils = timeUtils;
  }

  public void storeLastVisit(String idStream, CompletedCallback completedCallback) {
    this.completedCallback = completedCallback;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localStreamRepository.putLastStreamVisit(idStream, timeUtils.getCurrentTime());
    notifyCompleted();
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
