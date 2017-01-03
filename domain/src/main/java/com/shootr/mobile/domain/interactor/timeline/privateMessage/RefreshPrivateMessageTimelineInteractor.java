package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import java.util.Date;
import javax.inject.Inject;

public class RefreshPrivateMessageTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrTimelineService shootrTimelineService;

  private Callback<PrivateMessageTimeline> callback;
  private ErrorCallback errorCallback;
  private String idChannel;
  private String idTargetUser;
  private Long lastRefreshDate;
  private Boolean goneBackground;

  @Inject public RefreshPrivateMessageTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.shootrTimelineService = shootrTimelineService;
  }

  public void refreshStreamTimeline(String idChannel, String idTargetUser, Long lastRefreshDate, Boolean goneBackground,
      Callback<PrivateMessageTimeline> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idChannel = idChannel;
    this.idTargetUser = idTargetUser;
    this.lastRefreshDate = lastRefreshDate;
    this.goneBackground = goneBackground;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    executeSynchronized();
  }

  private synchronized void executeSynchronized() {
    try {
      PrivateMessageTimeline timeline =
          shootrTimelineService.refreshTimelinesForChannel(idChannel, idTargetUser,
              lastRefreshDate);
      notifyLoaded(timeline);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  //region Result
  private void notifyLoaded(final PrivateMessageTimeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
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
  //endregion
}