package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class DeleteHighlightedItemInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalTimelineRepository timelineRepository;
  private final PostExecutionThread postExecutionThread;

  private Interactor.CompletedCallback callback;
  private String idItem;
  private String resultType;
  private String idStream;

  @Inject public DeleteHighlightedItemInteractor(InteractorHandler interactorHandler,
      ExternalTimelineRepository timelineRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void deleteHighlightedItem(String resultType, String itemId, String idStream,
      Interactor.CompletedCallback callback) {
    this.callback = callback;
    this.idItem = itemId;
    this.resultType = resultType;
    this.idStream = idStream;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      timelineRepository.deleteHighlightedItem(resultType, idItem, idStream);
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
