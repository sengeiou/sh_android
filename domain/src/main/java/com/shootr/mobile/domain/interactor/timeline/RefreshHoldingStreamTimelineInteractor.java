package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.Date;
import javax.inject.Inject;

public class RefreshHoldingStreamTimelineInteractor implements Interactor {

  private static final Long REAL_TIME_INTERVAL = 60 * 1000L;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrTimelineService shootrTimelineService;
  private final LocaleProvider localeProvider;

  private Callback<Timeline> callback;
  private ErrorCallback errorCallback;
  private String idStream;
  private String idUser;
  private Long lastRefreshDate;
  private Boolean goneBackground;

  @Inject public RefreshHoldingStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService,
      LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.shootrTimelineService = shootrTimelineService;
    this.localeProvider = localeProvider;
  }

  public void refreshHoldingStreamTimeline(String streamId, String userId, Long lastRefreshDate,
      Boolean goneBackground, Callback<Timeline> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idStream = streamId;
    this.idUser = userId;
    this.goneBackground = goneBackground;
    this.lastRefreshDate = lastRefreshDate;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    executeSynchronized();
  }

  private synchronized void executeSynchronized() {
    try {
      long timestamp = new Date().getTime();
      Boolean isRealTime = !(goneBackground && timestamp - lastRefreshDate >= REAL_TIME_INTERVAL);
      Timeline timeline =
          shootrTimelineService.refreshHoldingTimelineForStream(idStream, idUser, isRealTime);
      notifyLoaded(timeline);
      shootrTimelineService.refreshTimelinesForActivity(localeProvider.getLocale());
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  //region Result
  private void notifyLoaded(final Timeline timeline) {
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
