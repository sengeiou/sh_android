package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class UnfollowStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final ActivityRepository localActivityRepository;

  private CompletedCallback callback;

  private String idStream;

  @Inject public UnfollowStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository,
      @Local ActivityRepository localActivityRepository) {
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localActivityRepository = localActivityRepository;
  }

  public void unfollow(String idStream, CompletedCallback callback) {
    this.callback = callback;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    localStreamRepository.unfollow(idStream);
    remoteStreamRepository.unfollow(idStream);
    localActivityRepository.updateUnFollowStreamOnActivity(idStream);
    notifyCompleted();
  }

  protected void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }
}
