package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import javax.inject.Inject;

public class RefreshHoldingStreamTimelineInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;

    private Callback<com.shootr.mobile.domain.Timeline> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private String idUser;

    @Inject public RefreshHoldingStreamTimelineInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
    }

    public void refreshHoldingStreamTimeline(String streamId, String userId, Callback<com.shootr.mobile.domain.Timeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.idStream = streamId;
        this.idUser = userId;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        try {
            com.shootr.mobile.domain.Timeline timeline = shootrTimelineService.refreshHoldingTimelineForStream(idStream, idUser);
            notifyLoaded(timeline);
            shootrTimelineService.refreshTimelinesForActivity();
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    //region Result
    private void notifyLoaded(final com.shootr.mobile.domain.Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
