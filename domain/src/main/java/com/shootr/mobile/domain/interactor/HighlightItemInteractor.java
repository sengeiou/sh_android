package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class HighlightItemInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalTimelineRepository timelineRepository;
  private final PostExecutionThread postExecutionThread;

  private Interactor.CompletedCallback callback;
  private String idItem;
  private String resultType;
  private String idStream;

  @Inject public HighlightItemInteractor(InteractorHandler interactorHandler,
      ExternalTimelineRepository timelineRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void highlightItem(String resultType, String itemId, String idStream,
      Interactor.CompletedCallback completedCallback) {
    this.callback = completedCallback;
    this.idItem = itemId;
    this.resultType = resultType;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      timelineRepository.highlightItem(resultType, idItem, idStream);
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
