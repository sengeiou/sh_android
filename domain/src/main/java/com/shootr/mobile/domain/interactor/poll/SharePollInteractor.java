package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.PollRepository;
import com.shootr.mobile.domain.repository.Remote;

public class SharePollInteractor implements Interactor {

  private final PollRepository remotePollRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private String idPoll;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  public SharePollInteractor(@Remote PollRepository remotePollRepository,
      InteractorHandler interactorHandler, PostExecutionThread postExecutionThread) {
    this.remotePollRepository = remotePollRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void sharePoll(String idPoll, CompletedCallback callback, ErrorCallback errorCallback) {
    this.idPoll = idPoll;
    this.completedCallback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remotePollRepository.sharePoll(idPoll);
      notifyCompleted();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  protected void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
