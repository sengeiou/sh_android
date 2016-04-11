package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.Date;
import javax.inject.Inject;

public class RefreshStreamTimelineInteractor implements Interactor {

    private static final Long REAL_TIME_INTERVAL = 60 * 1000L;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;
    private final LocaleProvider localeProvider;

    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private Long lastRefreshDate;
    private Boolean goneBackground;
    private Integer calls = 0;

    @Inject
    public RefreshStreamTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrTimelineService shootrTimelineService, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
        this.localeProvider = localeProvider;
    }

    public void refreshStreamTimeline(String streamId, Long lastRefreshDate, Boolean goneBackground,
      Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.idStream = streamId;
        this.lastRefreshDate = lastRefreshDate;
        this.goneBackground = goneBackground;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        try {
            long timestamp = new Date().getTime();
            Boolean isRealTime = calls != 0 && !(goneBackground && timestamp - lastRefreshDate >= REAL_TIME_INTERVAL);
            Timeline timeline = shootrTimelineService.refreshTimelinesForStream(idStream, isRealTime);
            notifyLoaded(timeline);
            shootrTimelineService.refreshTimelinesForActivity(localeProvider.getLocale());
            incrementCalls();
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    protected void incrementCalls() {
        calls++;
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
