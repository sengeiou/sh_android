package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class DeleteLocalShotsByStreamInteractor implements Interactor {

  private final ShotRepository localShotRepository;
  private final PostExecutionThread postExecutionThread;
  private final InteractorHandler interactorHandler;
  private String idStream;
  private CompletedCallback completedCallback;

  @Inject public DeleteLocalShotsByStreamInteractor(@Local ShotRepository localShotRepository,
      PostExecutionThread postExecutionThread, InteractorHandler interactorHandler) {
    this.localShotRepository = localShotRepository;
    this.postExecutionThread = postExecutionThread;
    this.interactorHandler = interactorHandler;
  }

  public void deleteShot(String idStream, CompletedCallback completedCallback) {
    this.idStream = idStream;
    this.completedCallback = completedCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localShotRepository.deleteShotsByStream(idStream);
    notifyLoaded();
  }

  private void notifyLoaded() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
