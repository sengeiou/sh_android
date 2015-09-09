package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.shot.ShootrTimelineService;
import javax.inject.Inject;

public class RefreshActivityTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;
    private final SessionRepository sessionRepository;

    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;

    @Inject public RefreshActivityTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      ShootrTimelineService shootrTimelineService, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
        this.sessionRepository = sessionRepository;
    }

    public void refreshActivityTimeline(Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        try {
            ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity();
            notifyLoaded(activityTimeline);
            shootrTimelineService.refreshTimelinesForStream(sessionRepository.getCurrentUser().getIdWatchingStream());
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    //region Result
    private void notifyLoaded(final ActivityTimeline timeline) {
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
