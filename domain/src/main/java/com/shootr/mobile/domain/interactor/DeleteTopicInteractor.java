package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class DeleteTopicInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalTimelineRepository timelineRepository;
  private final PostExecutionThread postExecutionThread;

  private CompletedCallback callback;
  private String resultType;
  private String idStream;

  @Inject public DeleteTopicInteractor(InteractorHandler interactorHandler,
      ExternalTimelineRepository timelineRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void deleteTopic(String resultType, String idStream,
      CompletedCallback callback) {
    this.callback = callback;
    this.resultType = resultType;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      timelineRepository.deleteTopic(resultType, idStream);
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
