package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.shot.ShootrTimelineService;
import javax.inject.Inject;

public class RefreshHoldingStreamTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;

    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private String idUser;

    @Inject public RefreshHoldingStreamTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
    }

    public void refreshHoldingStreamTimeline(String streamId, String userId, Callback<Timeline> callback, ErrorCallback errorCallback) {
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
            Timeline timeline = shootrTimelineService.refreshHoldingTimelineForStream(idStream, idUser);
            notifyLoaded(timeline);
            shootrTimelineService.refreshTimelinesForActivity();
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
