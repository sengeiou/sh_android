package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class CreateTopicInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalTimelineRepository timelineRepository;
  private final PostExecutionThread postExecutionThread;

  private Interactor.CompletedCallback callback;
  private String comment;
  private String resultType;
  private String idStream;
  private boolean notify;

  @Inject public CreateTopicInteractor(InteractorHandler interactorHandler,
      ExternalTimelineRepository timelineRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void createTopic(String resultType, String comment, String idStream, boolean notify,
      Interactor.CompletedCallback completedCallback) {
    this.callback = completedCallback;
    this.comment = comment;
    this.resultType = resultType;
    this.idStream = idStream;
    this.notify = notify;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      timelineRepository.createTopic(resultType, comment, idStream, notify);
      notifyCompleted();
    } catch (ShootrException error) {
      //TODO notifcar este error
    }
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }
}
