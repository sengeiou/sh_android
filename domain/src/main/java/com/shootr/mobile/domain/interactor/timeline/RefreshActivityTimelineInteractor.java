package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import javax.inject.Inject;

public class RefreshActivityTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private String language;
    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;

    @Inject public RefreshActivityTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
    }

    public void refreshActivityTimeline(String language, Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
        this.language = language;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        try {
            ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity(language);
            notifyLoaded(activityTimeline);
            User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
            if (user != null && user.getIdWatchingStream() != null) {
                shootrTimelineService.refreshTimelinesForStream(user.getIdWatchingStream(), false);
            }
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
